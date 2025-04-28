package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsOfMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpdateAlbumOfMusicUseCase
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater
import kotlinx.coroutines.flow.firstOrNull

class UpdateMusicUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val updateAlbumOfMusicUseCase: UpdateAlbumOfMusicUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
    private val updateArtistNameOfMusicUseCase: UpdateArtistNameOfMusicUseCase,
    private val musicFileUpdater: MusicFileUpdater,
) {
    private suspend fun getOrCreateArtist(
        artistName: String,
        music: Music,
    ): Artist {
        var existingNewArtist = artistRepository.getFromName(artistName = artistName)

        // It's a new artist, we need to create it.
        if (existingNewArtist == null) {
            existingNewArtist = Artist(
                artistName = artistName,
                cover = (music.cover as? Cover.CoverFile)?.fileCoverId?.let { fileCoverId ->
                    Cover.CoverFile(
                        fileCoverId = fileCoverId,
                    )
                }
            )
            artistRepository.upsert(artist = existingNewArtist)
        }

        return existingNewArtist
    }

    private suspend fun handleFirstArtistOfMusic(
        legacyMusic: Music,
        newMusicInformation: Music,
        previousArtist: Artist,
        newArtist: String,
        shouldPossessAlbum: Boolean,
    ) {
        // It's the same artist, we got nothing to do.
        if (previousArtist.artistName == newArtist) return

        val existingNewArtist = getOrCreateArtist(
            artistName = newArtist,
            music = newMusicInformation
        )

        // We update the link between the artist and the music.
        musicArtistRepository.upsertMusicIntoArtist(
            musicArtist = MusicArtist(
                musicId = newMusicInformation.musicId,
                artistId = existingNewArtist.artistId,
            ),
        )

        if (shouldPossessAlbum) {
            updateAlbumOfMusicUseCase(
                artistId = existingNewArtist.artistId,
                legacyMusic = legacyMusic,
                newAlbumName = newMusicInformation.album,
            )
        }

        musicArtistRepository.deleteMusicArtist(
            musicArtist = MusicArtist(
                musicId = legacyMusic.musicId,
                artistId = previousArtist.artistId,
            )
        )
        deleteArtistIfEmptyUseCase(artistId = previousArtist.artistId)
    }

    private suspend fun handleMultipleArtistsOfMusic(
        legacyMusic: Music,
        newMusicInformation: Music,
        previousArtists: List<Artist>,
        newArtistsName: List<String>,
        shouldFirstArtistPossessAlbum: Boolean,
    ) {
        // We first need to handle the first artist of the song (it's the one that possess the album of the song)
        handleFirstArtistOfMusic(
            legacyMusic = legacyMusic,
            newMusicInformation = newMusicInformation,
            previousArtist = previousArtists.first(),
            newArtist = newArtistsName.first(),
            shouldPossessAlbum = shouldFirstArtistPossessAlbum,
        )

        val previousArtistsWithoutFirstOne: List<Artist> = previousArtists
            .subList(1, previousArtists.size)

        // We will remove the link of the music to all its other previous artists
        previousArtistsWithoutFirstOne.forEach { artist ->
            musicArtistRepository.deleteMusicArtist(
                musicArtist = MusicArtist(
                    musicId = legacyMusic.musicId,
                    artistId = artist.artistId,
                )
            )
        }

        // We then link the music to its new artists
        newArtistsName.subList(1, newArtistsName.size).forEach { newArtistName ->
            val newArtist: Artist = getOrCreateArtist(
                artistName = newArtistName,
                music = newMusicInformation
            )
            musicArtistRepository.upsertMusicIntoArtist(
                musicArtist = MusicArtist(
                    musicId = newMusicInformation.musicId,
                    artistId = newArtist.artistId,
                )
            )
        }

        // We finally check if the previous artists can be deleted :
        previousArtistsWithoutFirstOne.forEach { artist ->
            deleteArtistIfEmptyUseCase(
                artistId = artist.artistId,
            )
        }
    }

    /**
     * Update a music.
     *
     * @param legacyMusic the information of the previous version of the music to update
     * (used for comparison between the legacy and new music information for better updating).
     * @param newMusicInformation the new music information to save.
     */
    suspend operator fun invoke(
        legacyMusic: Music,
        previousArtists: List<Artist>,
        newArtistsNames: List<String>,
        newMusicInformation: Music,
    ) {
        val shouldLinkMusicAlbumToAlbumArtist = newMusicInformation.albumArtist != null
                && legacyMusic.albumArtist != newMusicInformation.albumArtist

        val shouldDelegateToMultipleArtists = previousArtists.map { it.artistName } != newArtistsNames

        // We must unlink the album artist to the music
        if (newMusicInformation.albumArtist == null && legacyMusic.albumArtist != null) {
            val albumArtist: Artist? = artistRepository.getFromName(legacyMusic.albumArtist!!)

            albumArtist?.let {
                musicArtistRepository.deleteMusicArtist(
                    musicArtist = MusicArtist(
                        musicId = legacyMusic.musicId,
                        artistId = it.artistId,
                    )
                )
            }
        }

        if (shouldDelegateToMultipleArtists) {
            println("WIll delegate to multiple musics")
            handleMultipleArtistsOfMusic(
                legacyMusic = legacyMusic,
                newMusicInformation = newMusicInformation,
                previousArtists = previousArtists,
                newArtistsName = newArtistsNames,
                shouldFirstArtistPossessAlbum = newMusicInformation.albumArtist == null,
            )
        }

        if ((!shouldDelegateToMultipleArtists && legacyMusic.album != newMusicInformation.album) || shouldLinkMusicAlbumToAlbumArtist) {
            println("Album or album artist has changed")
            val artistForAlbum: Artist? = if (shouldLinkMusicAlbumToAlbumArtist) {
                getOrCreateArtist(
                    artistName = newMusicInformation.albumArtist.orEmpty(),
                    music = newMusicInformation,
                )
            } else {
                getArtistsOfMusicUseCase(music = legacyMusic)
                    .firstOrNull()
                    ?.firstOrNull()
            }

            artistForAlbum?.let { artist ->
                updateAlbumOfMusicUseCase(
                    legacyMusic = legacyMusic,
                    newAlbumName = newMusicInformation.album,
                    artistId = artist.artistId
                )

                if (shouldLinkMusicAlbumToAlbumArtist) {
                    // We will ensure that the album artist is linked to the music:
                    musicArtistRepository.upsertMusicIntoArtist(
                        musicArtist = MusicArtist(
                            musicId = newMusicInformation.musicId,
                            artistId = artistForAlbum.artistId
                        )
                    )
                }
            }

            /*
            If we changed the album artist of the music,
            we must check if the previous artist album can be deleted,
            and we must remove the previous link with the legacy album artist
             */
            if (shouldLinkMusicAlbumToAlbumArtist) {
                legacyMusic.albumArtist?.let { legacyAlbumArtist ->
                    val legacyArtist: Artist? = artistRepository.getFromName(artistName = legacyAlbumArtist)
                    legacyArtist?.artistId?.let { legacyArtistId ->
                        musicArtistRepository.deleteMusicArtist(
                            musicArtist = MusicArtist(
                                musicId = newMusicInformation.musicId,
                                artistId = legacyArtistId,
                            )
                        )
                        deleteArtistIfEmptyUseCase(artistId = legacyArtistId)
                    }
                }
            }
        }

        musicRepository.upsert(
            newMusicInformation.copy(
                artist = updateArtistNameOfMusicUseCase.buildUpdatedMusicArtistsString(music = newMusicInformation),
            )
        )
        musicFileUpdater.updateMusic(music = newMusicInformation)
    }
}
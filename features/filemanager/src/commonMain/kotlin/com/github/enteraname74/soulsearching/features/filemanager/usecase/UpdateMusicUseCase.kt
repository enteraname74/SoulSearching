package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsOfMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpdateAlbumOfMusicUseCase
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class UpdateMusicUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val updateAlbumOfMusicUseCase: UpdateAlbumOfMusicUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
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
                },
                dataMode = music.dataMode,
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
                dataMode = existingNewArtist.dataMode,
            ),
        )

        updateAlbumOfMusicUseCase(
            artistId = existingNewArtist.artistId,
            legacyMusic = legacyMusic,
            newAlbumName = newMusicInformation.album,
        )

        musicArtistRepository.delete(
            musicArtist = MusicArtist(
                musicId = legacyMusic.musicId,
                artistId = previousArtist.artistId,
                dataMode = legacyMusic.dataMode,
            )
        )
        deleteArtistIfEmptyUseCase(artistId = previousArtist.artistId)
    }

    private suspend fun handleMultipleArtistsOfMusic(
        legacyMusic: Music,
        newMusicInformation: Music,
        previousArtists: List<Artist>,
        newArtistsName: List<String>
    ) {
        // We first need to handle the first artist of the song (it's the one that possess the album of the song)
        handleFirstArtistOfMusic(
            legacyMusic = legacyMusic,
            newMusicInformation = newMusicInformation,
            previousArtist = previousArtists.first(),
            newArtist = newArtistsName.first(),
        )

        val previousArtistsWithoutFirstOne: List<Artist> = previousArtists
            .subList(1, previousArtists.size)

        // We will remove the link of the music to all its other previous artists
        previousArtistsWithoutFirstOne.forEach { artist ->
            musicArtistRepository.delete(
                musicArtist = MusicArtist(
                    musicId = legacyMusic.musicId,
                    artistId = artist.artistId,
                    dataMode = legacyMusic.dataMode,
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
                    dataMode = newMusicInformation.dataMode,
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

    private suspend fun updateLocal(
        legacyMusic: Music,
        previousArtists: List<Artist>,
        newArtistsNames: List<String>,
        newMusicInformation: Music,
        shouldUpdateMusicCover: Boolean,
    ): SoulResult<Unit> {
        if (previousArtists.map { it.artistName } != newArtistsNames) {
            handleMultipleArtistsOfMusic(
                legacyMusic = legacyMusic,
                newMusicInformation = newMusicInformation,
                previousArtists = previousArtists,
                newArtistsName = newArtistsNames,
            )
        } else if (legacyMusic.album != newMusicInformation.album) {
            val musicFirstArtist: Artist? = getArtistsOfMusicUseCase(musicId = legacyMusic.musicId)
                .firstOrNull()
                ?.firstOrNull()

            musicFirstArtist?.let { artist ->
                updateAlbumOfMusicUseCase(
                    legacyMusic = legacyMusic,
                    newAlbumName = newMusicInformation.album,
                    artistId = artist.artistId
                )
            }
        }

        // We need to retrieve the albumId that may have changed from the previous operations.
        musicRepository.upsert(
            music = newMusicInformation.copy(
                albumId = musicRepository.getFromId(
                    legacyMusic.musicId
                ).first()!!.albumId
            ),
        )
        musicFileUpdater.updateMusic(
            music = newMusicInformation,
            shouldUpdateMusicCover = shouldUpdateMusicCover,
        )

        return SoulResult.ofSuccess()
    }

    /**
     * Update a music.
     *
     * @param legacyMusic the information of the previous version of the music to update
     * (used for comparison between the legacy and new music information for better updating).
     * @param newArtistsNames the new list of artists the music should belong to.
     * @param newMusicInformation the new music information to save.
     *
     */
    suspend operator fun invoke(
        legacyMusic: Music,
        previousArtists: List<Artist>,
        newArtistsNames: List<String>,
        newMusicInformation: Music,
        newCoverId: UUID?,
    ): SoulResult<Unit> =
        when(legacyMusic.dataMode) {
            DataMode.Local -> {
                updateLocal(
                    legacyMusic = legacyMusic,
                    newMusicInformation = newMusicInformation,
                    previousArtists = previousArtists,
                    newArtistsNames = newArtistsNames,
                    shouldUpdateMusicCover = newCoverId != null,
                )
            }
            DataMode.Cloud -> {
                musicRepository.upsert(
                    music = newMusicInformation,
                    artists = newArtistsNames,
                    newCoverId = newCoverId,
                )
            }
        }
}
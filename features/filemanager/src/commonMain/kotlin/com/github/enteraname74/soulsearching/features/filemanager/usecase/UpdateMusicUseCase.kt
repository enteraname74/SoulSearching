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
        previousArtist: String,
        newArtist: String,
    ) {
        println("Comparing old artist: $previousArtist, with new one: $newArtist.")
        // It's the same artist, we got nothing to do.
        if (previousArtist == newArtist) return

        val legacyArtist = artistRepository.getFromName(artistName = previousArtist)
        val existingNewArtist = getOrCreateArtist(
            artistName = newArtist,
            music = newMusicInformation
        )

        println("Got legacy: $legacyArtist and new: $existingNewArtist")
        println("Will add link to new one: $existingNewArtist")

        // We update the link between the artist and the music.
        musicArtistRepository.upsertMusicIntoArtist(
            musicArtist = MusicArtist(
                musicId = newMusicInformation.musicId,
                artistId = existingNewArtist.artistId,
            ),
        )

        updateAlbumOfMusicUseCase(
            artistId = existingNewArtist.artistId,
            legacyMusic = legacyMusic,
            newAlbumName = newMusicInformation.album,
        )

        legacyArtist?.let { artist ->
            musicArtistRepository.deleteMusicArtist(
                musicArtist = MusicArtist(
                    musicId = legacyMusic.musicId,
                    artistId = legacyArtist.artistId,
                )
            )
            deleteArtistIfEmptyUseCase(artistId = artist.artistId)
        }
    }

    private suspend fun handleMultipleArtistsOfMusic(
        legacyMusic: Music,
        newMusicInformation: Music,
        previousArtistsName: List<String>,
        newArtistsName: List<String>
    ) {
        // We first need to handle the first artist of the song (it's the one that possess the album of the song)
        handleFirstArtistOfMusic(
            legacyMusic = legacyMusic,
            newMusicInformation = newMusicInformation,
            previousArtist = previousArtistsName.first(),
            newArtist = newArtistsName.first(),
        )

        val previousArtists: List<Artist> = previousArtistsName
            .subList(1, previousArtistsName.size)
            .mapNotNull { previousArtistName ->
                artistRepository.getFromName(artistName = previousArtistName)
            }

        // We will remove the link of the music to all its other previous artists
        previousArtists.forEach { artist ->
            println("Will delete link with artist: ${artist.artistName}")
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
            println("Will add link with artist: ${newArtist.artistName}")
            musicArtistRepository.upsertMusicIntoArtist(
                musicArtist = MusicArtist(
                    musicId = newMusicInformation.musicId,
                    artistId = newArtist.artistId,
                )
            )
        }

        // We finally check if the previous artists can be deleted :
        previousArtists.forEach { artist ->
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
        previousArtistsNames: List<String>,
        newArtistsNames: List<String>,
        newMusicInformation: Music,
    ) {
        if (previousArtistsNames != newArtistsNames) {
            println("Artists have changed!")
            handleMultipleArtistsOfMusic(
                legacyMusic = legacyMusic,
                newMusicInformation = newMusicInformation,
                previousArtistsName = previousArtistsNames,
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

        musicRepository.upsert(newMusicInformation)
        musicFileUpdater.updateMusic(music = newMusicInformation)
    }
}
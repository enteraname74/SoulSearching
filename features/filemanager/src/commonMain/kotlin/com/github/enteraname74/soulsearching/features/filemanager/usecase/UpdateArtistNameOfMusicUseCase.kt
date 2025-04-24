package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater
import kotlinx.coroutines.flow.firstOrNull

class UpdateArtistNameOfMusicUseCase(
    private val artistRepository: ArtistRepository,
    private val musicRepository: MusicRepository,
    private val musicFileUpdater: MusicFileUpdater
) {

    /**
     * Updates the artists names directly on the music item.
     * It manages multiple artists of the music.
     * @param legacyArtistName: the artist to remove from the music
     * @param newArtistName: the new artist name to add to the music
     * @param music: the music to modify
     */
    suspend operator fun invoke(
        legacyArtistName: String,
        newArtistName: String,
        music: Music,
    ) {
        val isAlbumArtist = legacyArtistName == music.albumArtist

        // We replace the legacy artist with the new one if it's not an album artist.
        val artistsOfMusic: ArrayList<String> = ArrayList(
            artistRepository.getArtistsOfMusic(music = music).firstOrNull()?.map { it.artistName } ?: emptyList()
        )

        // If the current
        if (isAlbumArtist) {
            artistsOfMusic.removeIf { artistName ->
                artistName == newArtistName
            }
        }

        artistsOfMusic.replaceAll { artistOfMusic ->
            if (artistOfMusic == legacyArtistName) {
                newArtistName
            } else {
                artistOfMusic
            }
        }

        // Additional check if the new one was already linked to the music
        val newArtistsOfMusic = artistsOfMusic
            .distinct()
            .joinToString(separator = ", ")

        val newMusicInformation: Music = music.copy(
            artist = newArtistsOfMusic,
            albumArtist = if (isAlbumArtist) {
                newArtistName
            } else {
                music.albumArtist
            }
        )

        musicRepository.upsert(
            newMusicInformation
        )
        musicFileUpdater.updateMusic(music = newMusicInformation)
    }
}
package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater
import kotlinx.coroutines.flow.firstOrNull

class UpdateArtistNameOfMusicUseCase(
    private val commonArtistUseCase: CommonArtistUseCase,
    private val musicRepository: MusicRepository,
    private val musicFileUpdater: MusicFileUpdater
) {

    suspend fun buildUpdatedMusicArtistsString(music: Music): String {
        val artistsOfMusic: ArrayList<String> = ArrayList(
            commonArtistUseCase.getArtistsOfMusic(music = music).firstOrNull()?.map { it.artistName } ?: emptyList()
        )

        val newArtistsOfMusic = artistsOfMusic
            .distinct()
            .joinToString(separator = ", ")

        return newArtistsOfMusic
    }

    /**
     * Updates the artists names directly on the music item.
     * It manages multiple artists of the music and the music album artist.
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

        val artistsOfMusic: ArrayList<String> = ArrayList(
            commonArtistUseCase.getArtistsOfMusic(music = music).firstOrNull()?.map { it.artistName } ?: emptyList()
        )

        /*
        If the artist was the album artist of the current music,
        and if this music has other artists,
        we will remove it from the saved string of artists for the music.
         */
        if (isAlbumArtist && artistsOfMusic.size > 1) {
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

        val newAlbumArtist = if (isAlbumArtist) {
            newArtistName
        } else {
            music.albumArtist
        }

        val newMusicInformation: Music = music.copy(
            artist = newArtistsOfMusic,
            albumArtist = newAlbumArtist
        )

        musicRepository.upsert(
            newMusicInformation
        )
        musicFileUpdater.updateMusic(music = newMusicInformation)
    }
}
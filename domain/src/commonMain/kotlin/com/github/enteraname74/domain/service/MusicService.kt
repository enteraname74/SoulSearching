package com.github.enteraname74.domain.service

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicWithCover
import java.util.UUID

/**
 * Service for handling music related work.
 */
interface MusicService {
    /**
     * Save a music.
     */
    suspend fun save(musicWithCover: MusicWithCover)

    /**
     * Delete a music.
     */
    suspend fun delete(musicId: UUID)

    /**
     * Update a music.
     *
     * @param legacyMusic the information of the previous version of the music to update
     * (used for comparison between the legacy and new music information for better updating).
     * @param newMusicInformation the new music information to save.
     */
    suspend fun update(
        legacyMusic: Music,
        newMusicInformation: Music
    )

    /**
     * Update the album of a music.
     * @param legacyMusic the legacy music information to update.
     * @param artistId the id of the music's artist.
     * @param newAlbumName the new album name of the music.
     */
    suspend fun updateAlbumOfMusic(
        legacyMusic: Music,
        artistId: UUID,
        newAlbumName: String
    )

    /**
     * Increment the total number of play of a music.
     */
    suspend fun updateNbPlayed(musicId: UUID)

    /**
     * Toggle the quick access state of a given music id.
     */
    suspend fun toggleQuickAccessState(musicId: UUID)

    /**
     * Toggle the favorite state of a given music id.
     */
    suspend fun toggleFavoriteState(musicId: UUID)
}
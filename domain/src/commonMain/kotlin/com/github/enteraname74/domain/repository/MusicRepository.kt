package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MusicRepository {

    /**
     * Save a music.
     */
    suspend fun upsert(musicWithCover: MusicWithCover)

    /**
     * Delete a music from its id.
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
     * Remove all songs of an Album.
     */
    suspend fun deleteAllMusicOfAlbum(album: String, artist: String)

    /**
     * Retrieve a music from its id.
     */
    fun getFromId(musicId: UUID): Flow<Music?>

    /**
     * Retrieves a flow of all Music.
     */
    fun getAll(): Flow<List<Music>>

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music>
}
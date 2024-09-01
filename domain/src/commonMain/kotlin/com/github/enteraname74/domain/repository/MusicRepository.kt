package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MusicRepository {

    /**
     * Upsert a music.
     */
    suspend fun upsert(music: Music)

    /**
     * Delete a music.
     */
    suspend fun delete(music: Music)

    /**
     * Remove all songs of an Album.
     */
    suspend fun deleteAllMusicOfAlbum(album: String, artist: String)

    /**
     * Retrieve a music from its id.
     */
    fun getFromId(musicId: UUID): Flow<Music?>

    suspend fun getFromPath(musicPath: String): Music?

    /**
     * Retrieves a flow of all Music.
     */
    fun getAll(): Flow<List<Music>>

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music>
}
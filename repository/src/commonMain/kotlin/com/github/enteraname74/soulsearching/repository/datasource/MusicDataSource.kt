package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a Music.
 */
interface MusicDataSource {
    /**
     * Inserts or updates a Music.
     */
    suspend fun upsert(music: Music)

    suspend fun upsertAll(musics: List<Music>)

    /**
     * Deletes a Music.
     */
    suspend fun delete(music: Music)

    suspend fun deleteAll(ids: List<UUID>)

    /**
     * Remove a Music from an Album.
     */
    suspend fun deleteMusicFromAlbum(album: String, artist: String)

    /**
     * Tries to retrieve a Music from its path.
     */
    suspend fun getFromPath(musicPath: String): Music?

    /**
     * Retrieve a music from its id.
     */
    fun getFromId(musicId: UUID): Flow<Music?>

    /**
     * Retrieves a flow of all Music, sorted by name asc.
     */
    fun getAll(): Flow<List<Music>>

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music>
}
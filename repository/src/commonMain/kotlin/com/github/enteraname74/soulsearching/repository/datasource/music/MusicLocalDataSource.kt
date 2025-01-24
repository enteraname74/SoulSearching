package com.github.enteraname74.soulsearching.repository.datasource.music

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a Music.
 */
interface MusicLocalDataSource {
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
    suspend fun deleteAll(dataMode: DataMode)

    /**
     * Tries to retrieve a Music from its path.
     */
    suspend fun getFromPath(musicPath: String): Music?

    /**
     * Retrieves all musics from a list of ids.
     */
    suspend fun getAll(musicIds: List<UUID>): List<Music>

    /**
     * Retrieve a music from its id.
     */
    fun getFromId(musicId: UUID): Flow<Music?>

    /**
     * Retrieves a flow of all Music, sorted by name asc.
     */
    fun getAll(dataMode: DataMode): Flow<List<Music>>

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music>

    /**
     * Replace an album by another one.
     */
    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID)
}
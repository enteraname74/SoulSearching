package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.util.FlowResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

interface MusicRepository {
    val uploadFlow: MutableStateFlow<FlowResult<Unit>>

    /**
     * Upsert a music.
     */
    suspend fun upsert(music: Music)

    suspend fun upsertAll(musics: List<Music>)

    /**
     * Delete a music.
     */
    suspend fun delete(music: Music)

    suspend fun deleteAll(ids: List<UUID>)

    suspend fun deleteAll(dataMode: DataMode)

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

    /**
     * Replace an album by another one.
     */
    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID)

    /**
     * Synchronize remote songs of the users with the cloud
     */
    suspend fun syncWithCloud(): SoulResult<List<UUID>>

    suspend fun uploadAllMusicToCloud(): SoulResult<Unit>

}
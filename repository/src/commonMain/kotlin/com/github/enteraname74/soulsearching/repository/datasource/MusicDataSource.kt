package com.github.enteraname74.soulsearching.repository.datasource

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import kotlinx.coroutines.flow.Flow
import java.util.*

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

    suspend fun deleteAllFromUnselectedFolders()

    /**
     * Retrieve a music from its id.
     */
    fun getFromId(musicId: UUID): Flow<Music?>

    suspend fun getAllIdsFromUnselectedFolders(): List<UUID>

    /**
     * Retrieves a flow of all Music, sorted by name asc.
     */
    fun getAll(): Flow<List<Music>>

    fun getAllFromQuickAccess(): Flow<List<Music>>

    fun getAllPaged(
        sortDirection: SortDirection,
        sortType: SortType,
    ): Flow<PagingData<Music>>

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music>

    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID)

    suspend fun cleanAllMusicCovers()

    suspend fun getAllMusicPath(): List<String>

    fun getStatisticsData(): Flow<List<Music>>
}
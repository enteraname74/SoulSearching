package com.github.enteraname74.soulsearching.repository.datasource.music

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid
import kotlin.time.Duration

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

    suspend fun deleteAll(ids: List<Uuid>)

    suspend fun deleteAllFromUnselectedFolders()

    /**
     * Retrieve a music from its id.
     */
    fun getFromId(musicId: Uuid): Flow<Music?>

    suspend fun getFromRemoteId(remoteId: String): Music?

    suspend fun getIdsFromRemoteIds(remoteIds: List<String>): List<Uuid>
    suspend fun getRemoteIdsFromIds(ids: List<Uuid>): List<String>

    fun getFromIds(ids: List<Uuid>): Flow<List<Music>>

    suspend fun getAllIdsFromUnselectedFolders(): List<Uuid>

    /**
     * Retrieves a flow of all Music, sorted by name asc.
     */
    fun getAll(): Flow<List<Music>>

    suspend fun getAllLocalMusic(): List<Music>

    suspend fun getAllSorted(): List<Music>

    fun getAllFromQuickAccess(): Flow<List<Music>>

    fun getAllPaged(): Flow<PagingData<Music>>

    /**
     * Retrieves all remote ids musics possessed by the User.
     */
    suspend fun getAllRemoteIdsPossessedByUser(): List<String>
    suspend fun getAllToSendToCloud(): List<Music>

    fun getAllPagedOfAlbum(albumId: Uuid): Flow<PagingData<Music>>

    fun getAllPagedByNameAscOfFolder(folder: String): Flow<PagingData<Music>>

    fun getAllPagedByNameAscOfMonth(month: String): Flow<PagingData<Music>>

    fun getAllPagedByNameAscOfPlaylist(playlistId: Uuid): Flow<PagingData<Music>>

    fun getAllPagedByNameAscOfArtist(artistId: Uuid): Flow<PagingData<Music>>

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: Uuid): List<Music>

    fun searchFromAlbum(
        albumId: Uuid,
        search: String,
    ): Flow<List<Music>>

    fun searchFromPlaylist(
        playlistId: Uuid,
        search: String,
    ): Flow<List<Music>>

    fun searchFromArtist(
        artistId: Uuid,
        search: String,
    ): Flow<List<Music>>

    fun searchFromFolder(
        folder: String,
        search: String,
    ): Flow<List<Music>>

    fun searchFromMonth(
        month: String,
        search: String,
    ): Flow<List<Music>>

    fun searchAll(
        search: String,
    ): Flow<List<Music>>

    suspend fun getAllMusicFromArtist(artistId: Uuid): List<Music>

    suspend fun getAllMusicFromPlaylist(playlistId: Uuid): List<Music>

    suspend fun getAllMusicFromMonth(month: String) : List<Music>

    suspend fun getAllMusicFromFolder(folder: String) : List<Music>

    fun getAlbumDuration(albumId: Uuid): Flow<Duration>
    fun getArtistDuration(artistId: Uuid): Flow<Duration>
    fun getPlaylistDuration(playlistId: Uuid): Flow<Duration>
    fun getMonthMusicsDuration(month: String): Flow<Duration>
    fun getFolderMusicsDuration(folder: String): Flow<Duration>

    suspend fun updateMusicsAlbum(newAlbumId: Uuid, legacyAlbumId: Uuid)

    suspend fun cleanAllMusicCovers()

    suspend fun getAllMusicLocalPath(): List<String>

    fun getMostListened(): Flow<List<Music>>

    fun getAllMonthMusics(): Flow<List<MonthMusicsPreview>>

    fun getMonthMusicPreview(month: String): Flow<MonthMusicsPreview?>

    fun getAllMusicFolders(): Flow<List<MusicFolderPreview>>

    fun getMusicFolderPreview(folder: String): Flow<MusicFolderPreview?>

    suspend fun getSoulMixMusics(totalPerFolder: Int): List<Music>

    suspend fun getFromInformation(
        musicName: String,
        albumId: Uuid,
    ): Music?

    suspend fun clearRemoteIds(remoteIds: List<String>)

    suspend fun deleteAllRemoteIds()
    suspend fun deleteNotExisting()
    suspend fun deleteSharedPlayedListMusics()
    suspend fun getFromPath(path: String): Music?

    fun observeDataChanged(): Flow<Unit>
}

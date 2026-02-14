package com.github.enteraname74.soulsearching.repository.datasource

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.time.Duration

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

    fun getAllPaged(): Flow<PagingData<Music>>

    fun getAllPagedOfAlbum(albumId: UUID): Flow<PagingData<Music>>

    fun getAllPagedByNameAscOfFolder(folder: String): Flow<PagingData<Music>>

    fun getAllPagedByNameAscOfMonth(month: String): Flow<PagingData<Music>>

    fun getAllPagedByNameAscOfPlaylist(playlistId: UUID): Flow<PagingData<Music>>

    fun getAllPagedByNameAscOfArtist(artistId: UUID): Flow<PagingData<Music>>

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music>

    fun searchFromAlbum(
        albumId: UUID,
        search: String,
    ): Flow<List<Music>>

    fun searchFromPlaylist(
        playlistId: UUID,
        search: String,
    ): Flow<List<Music>>

    fun searchFromArtist(
        artistId: UUID,
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

    suspend fun getAllMusicFromArtist(artistId: UUID): List<Music>

    suspend fun getAllMusicFromPlaylist(playlistId: UUID): List<Music>

    suspend fun getAllMusicFromMonth(month: String) : List<Music>

    suspend fun getAllMusicFromFolder(folder: String) : List<Music>

    fun getAlbumDuration(albumId: UUID): Flow<Duration>
    fun getArtistDuration(artistId: UUID): Flow<Duration>
    fun getPlaylistDuration(playlistId: UUID): Flow<Duration>
    fun getMonthMusicsDuration(month: String): Flow<Duration>
    fun getFolderMusicsDuration(folder: String): Flow<Duration>

    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID)

    suspend fun cleanAllMusicCovers()

    suspend fun getAllMusicPath(): List<String>

    fun getMostListened(): Flow<List<Music>>

    fun getAllMonthMusics(): Flow<List<MonthMusicsPreview>>

    fun getMonthMusicPreview(month: String): Flow<MonthMusicsPreview?>

    fun getAllMusicFolders(): Flow<List<MusicFolderPreview>>

    fun getMusicFolderPreview(folder: String): Flow<MusicFolderPreview?>

    suspend fun getSoulMixMusics(totalPerFolder: Int): List<Music>
}
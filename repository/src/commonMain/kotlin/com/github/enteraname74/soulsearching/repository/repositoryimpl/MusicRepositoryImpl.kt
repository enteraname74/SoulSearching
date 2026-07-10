package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.uuid.Uuid

/**
 * Repository for handling Music related work.
 */
class MusicRepositoryImpl(
    private val musicLocalDataSource: MusicLocalDataSource,
    private val musicRemoteDataSource: MusicRemoteDataSource,
) : MusicRepository {
    override suspend fun upsert(music: Music) {
        musicLocalDataSource.upsert(music = music)
    }

    override suspend fun upsertAll(musics: List<Music>) {
        musicLocalDataSource.upsertAll(musics = musics)
    }

    override suspend fun delete(music: Music) {
        musicLocalDataSource.delete(music = music)
    }

    override suspend fun deleteAll(ids: List<Uuid>) {
        musicLocalDataSource.deleteAll(ids = ids)
    }

    override suspend fun deleteAllFromUnselectedFolders() {
        musicLocalDataSource.deleteAllFromUnselectedFolders()
    }

    override suspend fun getRemoteIdsFromIds(ids: List<Uuid>): List<String> =
        musicLocalDataSource.getRemoteIdsFromIds(ids)

    override suspend fun getIdsFromRemoteIds(remoteIds: List<String>): List<Uuid> =
        musicLocalDataSource.getIdsFromRemoteIds(remoteIds)

    override suspend fun deleteRemotely(remoteIds: List<String>): SoulResult<Unit> =
        musicRemoteDataSource.delete(remoteIds)

    override fun getFromId(musicId: Uuid): Flow<Music?> = musicLocalDataSource.getFromId(
        musicId = musicId
    )

    override suspend fun getFromRemoteId(remoteId: String): Music? =
        musicLocalDataSource.getFromRemoteId(remoteId)

    override fun getFromIds(ids: List<Uuid>): Flow<List<Music>> =
        musicLocalDataSource.getFromIds(ids)

    override suspend fun getAllIdsFromUnselectedFolders(): List<Uuid> =
        musicLocalDataSource.getAllIdsFromUnselectedFolders()

    @Deprecated("Avoid fetching all music from DB because of performance issue")
    override fun getAll(): Flow<List<Music>> =
        musicLocalDataSource.getAll()

    override suspend fun getAllLocalMusic(): List<Music> =
        musicLocalDataSource.getAllLocalMusic()

    override suspend fun getAllSorted(): List<Music> =
        musicLocalDataSource.getAllSorted()

    override suspend fun getAllSorted(page: Int, pageSize: Int): List<Music> =
        musicLocalDataSource.getAllSorted(page = page, pageSize = pageSize)

    override fun getAllFromQuickAccess(): Flow<List<Music>> =
        musicLocalDataSource.getAllFromQuickAccess()

    override fun getAllPaged(): Flow<PagingData<Music>> =
        musicLocalDataSource.getAllPaged()

    override fun getAllPagedOfAlbum(albumId: Uuid): Flow<PagingData<Music>> =
        musicLocalDataSource.getAllPagedOfAlbum(albumId)

    override fun getAllPagedByNameAscOfFolder(folder: String): Flow<PagingData<Music>> =
        musicLocalDataSource.getAllPagedByNameAscOfFolder(folder)

    override fun getAllPagedByNameAscOfMonth(month: String): Flow<PagingData<Music>> =
        musicLocalDataSource.getAllPagedByNameAscOfMonth(month)

    override fun getAllPagedByNameAscOfPlaylist(playlistId: Uuid): Flow<PagingData<Music>> =
        musicLocalDataSource.getAllPagedByNameAscOfPlaylist(playlistId)

    override fun getAllPagedByNameAscOfArtist(artistId: Uuid): Flow<PagingData<Music>> =
        musicLocalDataSource.getAllPagedByNameAscOfArtist(artistId)

    override suspend fun getAllMusicFromAlbum(albumId: Uuid): List<Music> =
        musicLocalDataSource.getAllMusicFromAlbum(
            albumId = albumId
        )

    override suspend fun getAllMusicFromAlbum(albumId: Uuid, page: Int, pageSize: Int): List<Music> =
        musicLocalDataSource.getAllMusicFromAlbum(
            albumId = albumId,
            page = page,
            pageSize = pageSize,
        )

    override fun searchFromAlbum(
        albumId: Uuid,
        search: String
    ): Flow<List<Music>> =
        musicLocalDataSource.searchFromAlbum(
            albumId = albumId,
            search = search,
        )

    override fun searchFromPlaylist(
        playlistId: Uuid,
        search: String
    ): Flow<List<Music>> =
        musicLocalDataSource.searchFromPlaylist(
            playlistId = playlistId,
            search = search,
        )

    override fun searchFromArtist(
        artistId: Uuid,
        search: String
    ): Flow<List<Music>> =
        musicLocalDataSource.searchFromArtist(
            artistId = artistId,
            search = search,
        )

    override fun searchFromFolder(
        folder: String,
        search: String
    ): Flow<List<Music>> =
        musicLocalDataSource.searchFromFolder(
            folder = folder,
            search = search,
        )

    override fun searchFromMonth(
        month: String,
        search: String
    ): Flow<List<Music>> =
        musicLocalDataSource.searchFromMonth(
            month = month,
            search = search,
        )

    override fun searchAll(search: String): Flow<List<Music>> =
        musicLocalDataSource.searchAll(search)

    override suspend fun getAllMusicFromArtist(artistId: Uuid): List<Music> =
        musicLocalDataSource.getAllMusicFromArtist(artistId)

    override suspend fun getAllMusicFromArtist(artistId: Uuid, page: Int, pageSize: Int): List<Music> =
        musicLocalDataSource.getAllMusicFromArtist(
            artistId = artistId,
            page = page,
            pageSize = pageSize,
        )

    override suspend fun getAllMusicFromPlaylist(playlistId: Uuid): List<Music> =
        musicLocalDataSource.getAllMusicFromPlaylist(playlistId)

    override suspend fun getAllMusicFromPlaylist(playlistId: Uuid, page: Int, pageSize: Int): List<Music> =
        musicLocalDataSource.getAllMusicFromPlaylist(
            playlistId = playlistId,
            page = page,
            pageSize = pageSize,
        )

    override suspend fun getAllMusicFromMonth(month: String): List<Music> =
        musicLocalDataSource.getAllMusicFromMonth(month)

    override suspend fun getAllMusicFromFolder(folder: String): List<Music> =
        musicLocalDataSource.getAllMusicFromFolder(folder)

    override suspend fun getAllMusicFromFolder(folder: String, page: Int, pageSize: Int): List<Music> =
        musicLocalDataSource.getAllMusicFromFolder(
            folder = folder,
            page = page,
            pageSize = pageSize,
        )

    override fun getAlbumDuration(albumId: Uuid): Flow<Duration> =
        musicLocalDataSource.getAlbumDuration(albumId)

    override fun getArtistDuration(artistId: Uuid): Flow<Duration> =
        musicLocalDataSource.getArtistDuration(artistId)

    override fun getPlaylistDuration(playlistId: Uuid): Flow<Duration> =
        musicLocalDataSource.getPlaylistDuration(playlistId)

    override fun getMonthMusicsDuration(month: String): Flow<Duration> =
        musicLocalDataSource.getMonthMusicsDuration(month)

    override fun getFolderMusicsDuration(folder: String): Flow<Duration> =
        musicLocalDataSource.getFolderMusicsDuration(folder)

    override suspend fun updateMusicsAlbum(newAlbumId: Uuid, legacyAlbumId: Uuid) {
        musicLocalDataSource.updateMusicsAlbum(newAlbumId, legacyAlbumId)
    }

    override suspend fun cleanAllMusicCovers() {
        musicLocalDataSource.cleanAllMusicCovers()
    }

    override suspend fun getAllMusicLocalPath(): List<String> =
        musicLocalDataSource.getAllMusicLocalPath()

    override fun getMostListened(): Flow<List<Music>> =
        musicLocalDataSource.getMostListened()

    override fun getAllMonthMusics(): Flow<List<MonthMusicsPreview>> =
        musicLocalDataSource.getAllMonthMusics()

    override fun getMonthMusicPreview(month: String): Flow<MonthMusicsPreview?> =
        musicLocalDataSource.getMonthMusicPreview(month)

    override fun getAllMusicFolders(): Flow<List<MusicFolderPreview>> =
        musicLocalDataSource.getAllMusicFolders()

    override suspend fun getAllMusicFolders(page: Int, pageSize: Int): List<MusicFolderPreview> =
        musicLocalDataSource.getAllMusicFolders(page = page, pageSize = pageSize)

    override fun getMusicFolderPreview(folder: String): Flow<MusicFolderPreview?> =
        musicLocalDataSource.getMusicFolderPreview(folder)

    override suspend fun getSoulMixMusics(totalPerFolder: Int): List<Music> =
        musicLocalDataSource.getSoulMixMusics(totalPerFolder)

    override suspend fun getDeletedRemoteMusicIds(): List<String> {
        val allRemoteIds: List<String> = musicLocalDataSource.getAllRemoteIdsPossessedByUser()
        return musicRemoteDataSource.getDeletedRemoteMusicIds(idsToCheck = allRemoteIds)
    }

    override suspend fun getAllToSendToCloud(): List<Music> =
        musicLocalDataSource.getAllToSendToCloud()

    override suspend fun updateMusicToCloud(music: Music): CloudMusic? =
        musicRemoteDataSource.updateMusicToCloud(music).getOrNull()

    override suspend fun uploadMusicToCloud(music: Music): CloudMusic? =
        musicRemoteDataSource.uploadMusicToCloud(music).getOrNull()

    override suspend fun fetchUpdatedSongsFromCloud(lastSyncMillis: Long?): List<CloudMusic> {
        var page = 0

        val fetchedMusics: MutableList<CloudMusic> = mutableListOf()
        while (true) {
            val fetchedData = musicRemoteDataSource.getOfUser(
                lastUpdateAt = lastSyncMillis,
                maxPerPage = MAX_MUSICS_PER_PAGE,
                page = page
            )

            fetchedMusics += fetchedData
            if (fetchedData.size < MAX_MUSICS_PER_PAGE) break

            page += 1
        }

        return fetchedMusics
    }

    override suspend fun getFromInformation(
        musicName: String,
        albumId: Uuid
    ): Music? =
        musicLocalDataSource.getFromInformation(
            musicName = musicName,
            albumId = albumId,
        )

    override suspend fun clearRemoteIds(remoteIds: List<String>) {
        musicLocalDataSource.clearRemoteIds(remoteIds)
    }

    override suspend fun deleteAllRemoteIds() {
        musicLocalDataSource.deleteAllRemoteIds()
    }

    override suspend fun deleteNotExisting() {
        musicLocalDataSource.deleteNotExisting()
    }

    override suspend fun deleteSharedPlayedListMusics() {
        musicLocalDataSource.deleteSharedPlayedListMusics()
    }

    override suspend fun fetch(url: String): CloudMusic =
        musicRemoteDataSource.fetch(url = url)

    private companion object {
        const val MAX_MUSICS_PER_PAGE = 300
    }

    override suspend fun getFromPath(path: String): Music? =
        musicLocalDataSource.getFromPath(path)

    override fun observeDataChanged(): Flow<Unit> =
        musicLocalDataSource.observeDataChanged()
}

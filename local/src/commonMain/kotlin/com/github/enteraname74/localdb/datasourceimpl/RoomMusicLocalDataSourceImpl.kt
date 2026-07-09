package com.github.enteraname74.localdb.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import androidx.room3.useWriterConnection
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.DateUtils
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.dao.MusicDao
import com.github.enteraname74.localdb.ext.toRoomMusicArtists
import com.github.enteraname74.localdb.model.RoomCompleteMusic
import com.github.enteraname74.localdb.model.toRoomAlbum
import com.github.enteraname74.localdb.model.toRoomArtist
import com.github.enteraname74.localdb.model.toRoomMusic
import com.github.enteraname74.localdb.utils.PagingUtils
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Implementation of the MusicDataSource with Room's DAO.
 */
internal class RoomMusicLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
    private val settings: SoulSearchingSettings,
) : MusicLocalDataSource {
    override suspend fun upsert(music: Music) {
        appDatabase.useWriterConnection {
            appDatabase.artistDao.upsertAll(music.artists.map { it.toRoomArtist() })
            appDatabase.albumDao.upsert(music.album.toRoomAlbum())
            appDatabase.musicDao.upsert(
                music.copy(
                    lastUpdatedMillis = DateUtils.now(),
                ).toRoomMusic()
            )

            // Update links
            appDatabase.musicArtistDao.deleteOfMusic(music.musicId)
            appDatabase.musicArtistDao.upsertAll(
                roomMusicArtists = music.toRoomMusicArtists()
            )
        }
    }

    override suspend fun upsertAll(musics: List<Music>) {
        appDatabase.useWriterConnection {
            appDatabase.artistDao.upsertAll(
                roomArtists = musics.flatMap { music ->
                    music.artists.map { it.toRoomArtist() }
                }
            )

            appDatabase.albumDao.upsertAll(
                roomAlbums = musics.map { it.album.toRoomAlbum() }
            )
            appDatabase.musicDao.upsertAll(
                musics.map {
                    it.copy(
                        lastUpdatedMillis = DateUtils.now()
                    ).toRoomMusic()
                }
            )
            appDatabase.musicArtistDao.upsertAll(
                roomMusicArtists = musics.flatMap { it.toRoomMusicArtists() }
            )
        }
    }

    override suspend fun delete(music: Music) {
        appDatabase.useWriterConnection {
            appDatabase.musicDao.delete(roomMusic = music.toRoomMusic())
            appDatabase.albumDao.deleteAllEmpty()
            appDatabase.artistDao.deleteAllEmpty()
        }
    }

    override suspend fun deleteAll(ids: List<Uuid>) {
        appDatabase.useWriterConnection {
            appDatabase.musicDao.deleteAll(ids = ids)
            appDatabase.albumDao.deleteAllEmpty()
            appDatabase.artistDao.deleteAllEmpty()
        }
    }

    override suspend fun deleteAllFromUnselectedFolders() {
        appDatabase.useWriterConnection {
            appDatabase.musicDao.deleteFromUnselectedFolders()
            appDatabase.albumDao.deleteAllEmpty()
            appDatabase.artistDao.deleteAllEmpty()
        }
    }

    override suspend fun getAllIdsFromUnselectedFolders(): List<Uuid> =
        appDatabase.musicDao.getAllIdsFromUnselectedFolders()

    override fun getFromId(musicId: Uuid): Flow<Music?> {
        return appDatabase.musicDao.getFromId(
            musicId = musicId
        ).map { it?.toMusic() }
    }

    override suspend fun getFromRemoteId(remoteId: String): Music? =
        appDatabase.musicDao.getFromRemoteId(remoteId)?.toMusic()

    override suspend fun getIdsFromRemoteIds(remoteIds: List<String>): List<Uuid> =
        appDatabase.musicDao.getIdsFromRemoteIds(remoteIds)

    override suspend fun getRemoteIdsFromIds(ids: List<Uuid>): List<String> =
        appDatabase.musicDao.getRemoteIdsFromIds(ids)

    override fun getFromIds(ids: List<Uuid>): Flow<List<Music>> =
        appDatabase.musicDao.getFromIds(ids).map { list ->
            list
                .sortedBy { ids.indexOf(it.music.musicId) }
                .map { it.toMusic() }
        }

    override fun getAll(): Flow<List<Music>> {
        return appDatabase.musicDao.getAll().map { list ->
            list.map { it.toMusic() }
        }
    }

    override suspend fun getAllLocalMusic(): List<Music> =
        appDatabase.musicDao.getAllLocalMusic().map { it.toMusic() }

    override suspend fun getAllSorted(): List<Music> {
        val direction: SortDirection = SortDirection
            .from(settings.get(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_DIRECTION_KEY))
            ?: SortDirection.DEFAULT

        val type: SortType = SortType
            .from(settings.get(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_TYPE_KEY))
            ?: SortType.DEFAULT

        return with(appDatabase.musicDao) {
            when (direction) {
                SortDirection.ASC -> {
                    when (type) {
                        SortType.NAME -> getAllByNameAsc()
                        SortType.ADDED_DATE -> getAllByDateAsc()
                        SortType.NB_PLAYED -> getAllByNbPlayedAsc()
                    }
                }

                SortDirection.DESC -> {
                    when (type) {
                        SortType.NAME -> getAllByNameDesc()
                        SortType.ADDED_DATE -> getAllByDateDesc()
                        SortType.NB_PLAYED -> getAllByNbPlayedDesc()
                    }
                }
            }
        }.map { it.toMusic() }
    }

    override fun getAllFromQuickAccess(): Flow<List<Music>> =
        appDatabase.musicDao.getAllFromQuickAccess().map { list ->
            list.map { it.toMusic() }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPaged(): Flow<PagingData<Music>> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_DIRECTION_KEY)
            .flatMapLatest { direction ->
                settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_TYPE_KEY)
                    .flatMapLatest { type ->
                        val sortDirection = SortDirection.from(direction) ?: SortDirection.DEFAULT
                        val sortType = SortType.from(type) ?: SortType.DEFAULT

                        Pager(
                            config = PagingConfig(
                                pageSize = PagingUtils.PAGE_SIZE,
                                enablePlaceholders = false,
                            ),
                            pagingSourceFactory = {
                                when (sortDirection) {
                                    SortDirection.ASC -> {
                                        when (sortType) {
                                            SortType.NAME -> appDatabase.musicDao.getAllPagedByNameAsc()
                                            SortType.ADDED_DATE -> appDatabase.musicDao.getAllPagedByDateAsc()
                                            SortType.NB_PLAYED -> appDatabase.musicDao.getAllPagedByNbPlayedAsc()
                                        }
                                    }

                                    SortDirection.DESC -> {
                                        when (sortType) {
                                            SortType.NAME -> appDatabase.musicDao.getAllPagedByNameDesc()
                                            SortType.ADDED_DATE -> appDatabase.musicDao.getAllPagedByDateDesc()
                                            SortType.NB_PLAYED -> appDatabase.musicDao.getAllPagedByNbPlayedDesc()
                                        }
                                    }
                                }
                            }
                        ).flow.map { pagingData ->
                            pagingData.map { it.toMusic() }
                        }
                    }
            }

    override suspend fun getAllRemoteIdsPossessedByUser(): List<String> =
        appDatabase.musicDao.getAllRemoteIdsPossessedByUser()

    override suspend fun getAllToSendToCloud(): List<Music> =
        appDatabase.musicDao.getAllToSendToCloud().map { it.toMusic() }

    override fun getAllPagedOfAlbum(albumId: Uuid): Flow<PagingData<Music>> =
        withPaging { getAllPagedOfAlbum(albumId) }

    override fun getAllPagedByNameAscOfFolder(folder: String): Flow<PagingData<Music>> =
        withPaging { getAllPagedByNameAscOfFolder(folder) }

    override fun getAllPagedByNameAscOfMonth(month: String): Flow<PagingData<Music>> =
        withPaging { getAllPagedByNameAscOfMonth(month) }

    override fun getAllPagedByNameAscOfPlaylist(playlistId: Uuid): Flow<PagingData<Music>> =
        withPaging { getAllPagedByNameAscOfPlaylist(playlistId) }

    override fun getAllPagedByNameAscOfArtist(artistId: Uuid): Flow<PagingData<Music>> =
        withPaging { getAllPagedByNameAscOfArtist(artistId) }

    override suspend fun getAllMusicFromAlbum(albumId: Uuid): List<Music> =
        appDatabase.musicDao.getAllMusicFromAlbum(
            albumId = albumId
        ).map { it.toMusic() }

    override fun searchFromAlbum(
        albumId: Uuid,
        search: String
    ): Flow<List<Music>> =
        appDatabase.musicDao.searchFromAlbum(
            albumId = albumId,
            search = search,
        ).map { list ->
            list.map { it.toMusic() }
        }

    override fun searchFromPlaylist(
        playlistId: Uuid,
        search: String
    ): Flow<List<Music>> =
        appDatabase.musicDao.searchFromPlaylist(
            playlistId = playlistId,
            search = search,
        ).map { list ->
            list.map { it.toMusic() }
        }

    override fun searchFromArtist(
        artistId: Uuid,
        search: String
    ): Flow<List<Music>> =
        appDatabase.musicDao.searchFromArtist(
            artistId = artistId,
            search = search,
        ).map { list ->
            list.map { it.toMusic() }
        }

    override fun searchFromFolder(
        folder: String,
        search: String
    ): Flow<List<Music>> =
        appDatabase.musicDao.searchFromFolder(
            folder = folder,
            search = search,
        ).map { list ->
            list.map { it.toMusic() }
        }

    override fun searchFromMonth(
        month: String,
        search: String
    ): Flow<List<Music>> =
        appDatabase.musicDao.searchFromMonth(
            month = month,
            search = search,
        ).map { list ->
            list.map { it.toMusic() }
        }

    override fun searchAll(search: String): Flow<List<Music>> =
        appDatabase.musicDao.searchAll(search).map { list ->
            list.map { it.toMusic() }
        }

    override suspend fun getAllMusicFromArtist(artistId: Uuid): List<Music> =
        appDatabase.musicDao.getAllMusicFromArtist(artistId).map { it.toMusic() }

    override suspend fun getAllMusicFromPlaylist(playlistId: Uuid): List<Music> =
        appDatabase.musicDao.getAllMusicFromPlaylist(playlistId).map { it.toMusic() }

    override suspend fun getAllMusicFromMonth(month: String): List<Music> =
        appDatabase.musicDao.getAllMusicFromMonth(month).map { it.toMusic() }

    override suspend fun getAllMusicFromFolder(folder: String): List<Music> =
        appDatabase.musicDao.getAllMusicFromFolder(folder).map { it.toMusic() }

    override fun getAlbumDuration(albumId: Uuid): Flow<Duration> =
        appDatabase.musicDao.getAlbumDuration(albumId).map { it.milliseconds }

    override fun getArtistDuration(artistId: Uuid): Flow<Duration> =
        appDatabase.musicDao.getArtistDuration(artistId).map { it.milliseconds }

    override fun getPlaylistDuration(playlistId: Uuid): Flow<Duration> =
        appDatabase.musicDao.getPlaylistDuration(playlistId).map { it.milliseconds }

    override fun getMonthMusicsDuration(month: String): Flow<Duration> =
        appDatabase.musicDao.getMonthMusicsDuration(month).map { it.milliseconds }

    override fun getFolderMusicsDuration(folder: String): Flow<Duration> =
        appDatabase.musicDao.getFolderMusicsDuration(folder).map { it.milliseconds }

    override suspend fun updateMusicsAlbum(newAlbumId: Uuid, legacyAlbumId: Uuid) {
        appDatabase.musicDao.updateMusicsAlbum(newAlbumId, legacyAlbumId)
    }

    override suspend fun cleanAllMusicCovers() {
        appDatabase.musicDao.cleanAllMusicCovers()
    }

    override suspend fun getAllMusicLocalPath(): List<String> =
        appDatabase.musicDao.getAllMusicLocalPath()

    override fun getMostListened(): Flow<List<Music>> =
        appDatabase.musicDao.getMostListened().map { list ->
            list.map { it.toMusic() }
        }

    override fun getAllMonthMusics(): Flow<List<MonthMusicsPreview>> =
        appDatabase.musicDao.getAllMonthMusics().map { list ->
            list.map { it.toMonthMusicsPreview() }
        }

    override fun getMonthMusicPreview(month: String): Flow<MonthMusicsPreview?> =
        appDatabase.musicDao.getMonthMusicPreview(month).map { it?.toMonthMusicsPreview() }

    override fun getAllMusicFolders(): Flow<List<MusicFolderPreview>> =
        appDatabase.musicDao.getAllMusicFoldersPreview().map { list ->
            list.map { it.toMusicFolderPreview() }
        }

    override fun getMusicFolderPreview(folder: String): Flow<MusicFolderPreview?> =
        appDatabase.musicDao.getMusicFolderPreview(folder).map { it?.toMusicFolderPreview() }

    override suspend fun getSoulMixMusics(totalPerFolder: Int): List<Music> {
        val folders: List<String> = appDatabase.musicDao.getAllMusicFolders()

        return buildList {
            folders.forEach { folder ->
                addAll(
                    appDatabase.musicDao.getSoulMixMusics(
                        totalPerFolder = totalPerFolder,
                        folder = folder,
                    ).map { it.toMusic() }
                )
            }
        }.shuffled()
    }

    override suspend fun getFromInformation(
        musicName: String,
        albumId: Uuid
    ): Music? =
        appDatabase.musicDao.getFromInformation(
            musicName = musicName,
            albumId = albumId,
        )?.toMusic()

    override suspend fun clearRemoteIds(remoteIds: List<String>) {
        appDatabase.musicDao.clearRemoteIds(remoteIds)
    }

    override suspend fun deleteAllRemoteIds() {
        appDatabase.musicDao.deleteAllRemoteIds()
    }

    override suspend fun deleteNotExisting() {
        appDatabase.musicDao.deleteNotExisting()
    }

    override suspend fun deleteSharedPlayedListMusics() {
        appDatabase.musicDao.deleteSharedPlayedListMusics()
    }

    override suspend fun getFromPath(path: String): Music? =
        appDatabase.musicDao.getFromPath(path)?.toMusic()

    override fun observeDataChanged(): Flow<Unit> =
        appDatabase.invalidationTracker.createFlow(
            "RoomMusic",
            "RoomAlbum",
            "RoomArtist",
            "RoomPlaylist",
            "RoomMusicPlaylist",
            "RoomMusicArtist",
            emitInitialState = false,
        ).map { }

    private fun withPaging(
        source: MusicDao.() -> PagingSource<Int, RoomCompleteMusic>,
    ): Flow<PagingData<Music>> =
        Pager(
            config = PagingConfig(
                pageSize = PagingUtils.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                with(appDatabase.musicDao) {
                    source()
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toMusic() }
        }
}

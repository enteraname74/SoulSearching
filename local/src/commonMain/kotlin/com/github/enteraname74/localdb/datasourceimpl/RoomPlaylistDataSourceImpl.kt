package com.github.enteraname74.localdb.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toPlaylist
import com.github.enteraname74.localdb.model.toPlaylistWIthMusics
import com.github.enteraname74.localdb.model.toRoomPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.localdb.utils.PagingUtils
import com.github.enteraname74.soulsearching.repository.datasource.PlaylistDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class RoomPlaylistDataSourceImpl(
    private val appDatabase: AppDatabase,
    private val settings: SoulSearchingSettings,
) : PlaylistDataSource {
    override suspend fun upsert(playlist: Playlist) {
        appDatabase.playlistDao.upsert(
            roomPlaylist = playlist.toRoomPlaylist()
        )
    }

    override suspend fun upsertAll(playlists: List<Playlist>) {
        appDatabase.playlistDao.upsertAll(
            roomPlaylists = playlists.map { it.toRoomPlaylist() }
        )
    }

    override suspend fun delete(playlist: Playlist) {
        appDatabase.playlistDao.delete(
            roomPlaylist = playlist.toRoomPlaylist()
        )
    }

    override suspend fun deleteAll(playlistIds: List<UUID>) {
        appDatabase.playlistDao.deleteAll(
            ids = playlistIds,
        )
    }

    override fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistWithMusics().map { list ->
            list.map { it.toPlaylistWIthMusics() }
        }
    }

    override fun getFromId(playlistId: UUID): Flow<Playlist?> {
        return appDatabase.playlistDao.getFromId(
            playlistId = playlistId
        ).map { it?.toPlaylist() }
    }

    override fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?> {
        return appDatabase.playlistDao.getPlaylistWithMusics(playlistId = playlistId)
            .map { it?.toPlaylistWIthMusics() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPaged(): Flow<PagingData<PlaylistPreview>> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_DIRECTION_KEY).flatMapLatest { direction ->
            settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_TYPE_KEY).flatMapLatest { type ->
                val sortDirection = SortDirection.from(direction) ?: SortDirection.DEFAULT
                val sortType = SortType.from(type) ?: SortType.DEFAULT

                Pager(
                    config = PagingConfig(
                        pageSize = PagingUtils.PAGE_SIZE,
                        enablePlaceholders = false,
                    ),
                    pagingSourceFactory = {
                        when(sortDirection) {
                            SortDirection.ASC -> {
                                when (sortType) {
                                    SortType.NAME -> appDatabase.playlistDao.getAllPagedByNameAsc()
                                    SortType.ADDED_DATE -> appDatabase.playlistDao.getAllPagedByDateAsc()
                                    SortType.NB_PLAYED -> appDatabase.playlistDao.getAllPagedByNbPlayedAsc()
                                }
                            }
                            SortDirection.DESC -> {
                                when (sortType) {
                                    SortType.NAME -> appDatabase.playlistDao.getAllPagedByNameDesc()
                                    SortType.ADDED_DATE -> appDatabase.playlistDao.getAllPagedByDateDesc()
                                    SortType.NB_PLAYED -> appDatabase.playlistDao.getAllPagedByNbPlayedDesc()
                                }
                            }
                        }
                    }
                ).flow.map { pagingData ->
                    pagingData.map { it.toPlaylistPreview() }
                }
            }
        }

    override suspend fun cleanAllCovers() {
        appDatabase.playlistDao.cleanAllCovers()
    }

    override fun getAllFromQuickAccess(): Flow<List<PlaylistPreview>> =
        appDatabase.playlistDao.getAllFromQuickAccess().map { list ->
            list.map { it.toPlaylistPreview() }
        }

    override fun getMostListened(): Flow<List<PlaylistPreview>> =
        appDatabase.playlistDao.getMostListened().map { list ->
            list.map { it.toPlaylistPreview() }
        }

    override fun getPlaylistPreview(playlistId: UUID): Flow<PlaylistPreview?> =
        appDatabase.playlistDao.getPlaylistPreview(playlistId).map { it?.toPlaylistPreview() }
}
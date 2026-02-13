package com.github.enteraname74.localdb.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoomAlbum
import com.github.enteraname74.localdb.utils.PagingUtils
import com.github.enteraname74.soulsearching.repository.datasource.AlbumDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementation of the AlbumDataSource with Room's DAO.
 */
internal class RoomAlbumDataSourceImpl(
    private val appDatabase: AppDatabase,
    private val settings: SoulSearchingSettings,
) : AlbumDataSource {
    override suspend fun upsert(album: Album) {
        appDatabase.albumDao.upsert(
            roomAlbum = album.toRoomAlbum()
        )
    }

    override suspend fun upsertAll(albums: List<Album>) {
        appDatabase.albumDao.upsertAll(albums.map { it.toRoomAlbum() })
    }

    override suspend fun delete(album: Album) {
        appDatabase.albumDao.delete(
            roomAlbum = album.toRoomAlbum()
        )
    }

    override suspend fun deleteAll(ids: List<UUID>) {
        appDatabase.albumDao.deleteAll(
            ids = ids,
        )
    }

    override suspend fun getAlbumNamesContainingSearch(search: String): List<String> =
        appDatabase.albumDao.getAlbumNamesContainingSearch(search)

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> {
        return appDatabase.albumDao.getAllAlbumsFromArtist(
            artistId = artistId
        ).map { list ->
            list.map {
                it.toAlbum()
            }
        }
    }

    override fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>> =
        appDatabase.albumDao.getAllAlbumsWithMusicsFromArtist(
            artistId = artistId
        ).map { list ->
            list.map {
                it.toAlbumWithMusics()
            }
        }


    override fun getFromId(albumId: UUID): Flow<Album?> {
        return appDatabase.albumDao.getFromId(
            albumId = albumId
        ).map { it?.toAlbum() }
    }

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> {
        return appDatabase.albumDao.getAlbumWithMusics(
            albumId = albumId
        ).map { it?.toAlbumWithMusics() }
    }

    override fun getAll(): Flow<List<Album>> {
        return appDatabase.albumDao.getAll().map { list ->
            list.map { it.toAlbum() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPaged(): Flow<PagingData<AlbumPreview>> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_DIRECTION_KEY).flatMapLatest { direction ->
            settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_TYPE_KEY).flatMapLatest { type ->
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
                                    SortType.NAME -> appDatabase.albumDao.getAllPagedByNameAsc()
                                    SortType.ADDED_DATE -> appDatabase.albumDao.getAllPagedByDateAsc()
                                    SortType.NB_PLAYED -> appDatabase.albumDao.getAllPagedByNbPlayedAsc()
                                }
                            }
                            SortDirection.DESC -> {
                                when (sortType) {
                                    SortType.NAME -> appDatabase.albumDao.getAllPagedByNameDesc()
                                    SortType.ADDED_DATE -> appDatabase.albumDao.getAllPagedByDateDesc()
                                    SortType.NB_PLAYED -> appDatabase.albumDao.getAllPagedByNbPlayedDesc()
                                }
                            }
                        }
                    }
                ).flow.map { pagingData ->
                    pagingData.map { it.toAlbumPreview() }
                }
            }
        }

    override fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumWithMusics().map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override fun getAllFromQuickAccess(): Flow<List<AlbumPreview>> =
        appDatabase.albumDao.getAllFromQuickAccess().map { list ->
            list.map { it.toAlbumPreview() }
        }

    override suspend fun cleanAllMusicCovers() {
        appDatabase.albumDao.cleanAllMusicCovers()
    }

    override suspend fun getDuplicatedAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? =
        appDatabase.albumDao.getDuplicatedAlbum(
            albumId = albumId,
            albumName = albumName,
            artistId = artistId,
        )?.toAlbum()
}
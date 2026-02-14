package com.github.enteraname74.localdb.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toArtist
import com.github.enteraname74.localdb.model.toArtistWithMusics
import com.github.enteraname74.localdb.model.toRoomArtist
import com.github.enteraname74.localdb.utils.PagingUtils
import com.github.enteraname74.soulsearching.repository.datasource.ArtistDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementation of the ArtistDataSource with Room's DAO.
 */
internal class RoomArtistDataSourceImpl(
    private val appDatabase: AppDatabase,
    private val settings: SoulSearchingSettings,
) : ArtistDataSource {
    override suspend fun upsert(artist: Artist) {
        appDatabase.artistDao.upsert(
            roomArtist = artist.toRoomArtist()
        )
    }

    override suspend fun upsertAll(artists: List<Artist>) {
        appDatabase.artistDao.upsertAll(artists.map { it.toRoomArtist() })
    }

    override suspend fun deleteAll(artist: Artist) {
        appDatabase.artistDao.delete(
            roomArtist = artist.toRoomArtist()
        )
    }

    override suspend fun deleteAll(artistsIds: List<UUID>) {
        appDatabase.artistDao.deleteAll(
            ids = artistsIds,
        )
    }

    override suspend fun getArtistNamesContainingSearch(search: String): List<String> =
        appDatabase.artistDao.getArtistNamesContainingSearch(search)

    override suspend fun toggleCoverFolderMode(isActivated: Boolean) {
        if (isActivated) {
            appDatabase.artistDao.activateCoverFolderMode(
                key = SoulSearchingSettingsKeys.Cover.ARTIST_COVER_FOLDER_RETRIEVER.key
            )
        } else {
            appDatabase.artistDao.deactivateCoverFolderMode()
        }
    }

    override fun getFromId(artistId: UUID): Flow<Artist?> {
        return appDatabase.artistDao.getFromId(
            artistId = artistId
        ).map { it?.toArtist() }
    }

    override fun getAll(): Flow<List<Artist>> {
        return appDatabase.artistDao.getAll().map { list ->
            list.map { it.toArtist() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPaged(): Flow<PagingData<ArtistPreview>> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_DIRECTION_KEY).flatMapLatest { direction ->
            settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_TYPE_KEY).flatMapLatest { type ->
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
                                    SortType.NAME -> appDatabase.artistDao.getAllPagedByNameAsc()
                                    SortType.ADDED_DATE -> appDatabase.artistDao.getAllPagedByDateAsc()
                                    SortType.NB_PLAYED -> appDatabase.artistDao.getAllPagedByNbPlayedAsc()
                                }
                            }
                            SortDirection.DESC -> {
                                when (sortType) {
                                    SortType.NAME -> appDatabase.artistDao.getAllPagedByNameDesc()
                                    SortType.ADDED_DATE -> appDatabase.artistDao.getAllPagedByDateDesc()
                                    SortType.NB_PLAYED -> appDatabase.artistDao.getAllPagedByNbPlayedDesc()
                                }
                            }
                        }
                    }
                ).flow.map { pagingData ->
                    pagingData.map { it.toArtistPreview() }
                }
            }
        }

    override suspend fun getFromName(artistName: String): Artist? {
        return appDatabase.artistDao.getFromName(
            artistName = artistName
        )?.toArtist()
    }

    override suspend fun getAllFromName(artistsNames: List<String>): List<Artist> =
        appDatabase.artistDao.getAllFromName(
            artistsNames
        ).map { it.toArtist() }

    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> {
        return appDatabase.artistDao.getArtistWithMusics(
            artistId = artistId
        ).map { it?.toArtistWithMusics() }
    }

    override fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> =
        appDatabase.artistDao.getArtistsOfMusic(musicId = musicId).map { list ->
            list.map { it.toArtist() }
        }

    override fun getAllFromQuickAccess(): Flow<List<ArtistPreview>> =
        appDatabase.artistDao.getAllFromQuickAccess().map { list ->
            list.map { it.toArtistPreview() }
        }

    override suspend fun getDuplicatedArtist(
        artistId: UUID,
        artistName: String
    ): ArtistWithMusics? =
        appDatabase.artistDao.getDuplicatedArtist(
            artistId = artistId,
            artistName = artistName,
        )?.toArtistWithMusics()

    override fun getArtistsWistMostMusics(): Flow<List<ArtistPreview>> =
        appDatabase.artistDao.getArtistsWistMostMusics().map { list ->
            list.map { it.toArtistPreview() }
        }

    override suspend fun cleanAllCovers() {
        appDatabase.artistDao.cleanAllCovers()
    }

    override fun getMostListened(): Flow<List<ArtistPreview>> =
        appDatabase.artistDao.getMostListened().map { list ->
            list.map { it.toArtistPreview() }
        }
}
package com.github.enteraname74.localdb.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.collection.RoomCollectionAlbum
import com.github.enteraname74.localdb.model.collection.RoomCollectionArtist
import com.github.enteraname74.localdb.model.collection.RoomCollectionPlaylist
import com.github.enteraname74.localdb.model.collection.toCollection
import com.github.enteraname74.localdb.model.collection.toRoomCollection
import com.github.enteraname74.localdb.utils.PagingUtils
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import com.github.enteraname74.soulsearching.repository.datasource.collection.CollectionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlin.uuid.Uuid

internal class RoomCollectionLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
    private val settings: SoulSearchingSettings,
) : CollectionLocalDataSource {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPaged(): Flow<PagingData<CollectionPreview>> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_COLLECTIONS_DIRECTION_KEY).flatMapLatest { direction ->
            settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_COLLECTIONS_TYPE_KEY).flatMapLatest { type ->
                val sortDirection = SortDirection.from(direction) ?: SortDirection.DEFAULT
                val sortType = SortType.from(type) ?: SortType.DEFAULT
                Pager(
                    config = PagingConfig(
                        pageSize = PagingUtils.PAGE_SIZE,
                        enablePlaceholders = false,
                    ),
                    pagingSourceFactory = {
                        when (sortDirection) {
                            SortDirection.ASC -> when (sortType) {
                                SortType.NAME -> appDatabase.collectionDao.getAllPagedByNameAsc()
                                SortType.ADDED_DATE -> appDatabase.collectionDao.getAllPagedByDateAsc()
                                SortType.NB_PLAYED -> appDatabase.collectionDao.getAllPagedByNbPlayedAsc()
                            }
                            SortDirection.DESC -> when (sortType) {
                                SortType.NAME -> appDatabase.collectionDao.getAllPagedByNameDesc()
                                SortType.ADDED_DATE -> appDatabase.collectionDao.getAllPagedByDateDesc()
                                SortType.NB_PLAYED -> appDatabase.collectionDao.getAllPagedByNbPlayedDesc()
                            }
                        }
                    },
                ).flow.map { pagingData -> pagingData.map { it.toCollectionPreview() } }
            }
        }

    override fun getAll(): Flow<List<CollectionPreview>> =
        appDatabase.collectionDao.getAllPreviews().map { previews ->
            previews.map { it.toCollectionPreview() }
        }

    override fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?> =
        appDatabase.collectionDao.getCollectionPreview(collectionId).map {
            it?.toCollectionPreview()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFromIds(collectionIds: List<Uuid>): Flow<List<CollectionWithMusics>> =
        appDatabase.collectionDao.getFromIds(collectionIds).flatMapLatest { roomCollections ->
            val sortedCollections = roomCollections.sortedBy {
                collectionIds.indexOf(it.collectionId)
            }
            if (sortedCollections.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    sortedCollections.map { collection ->
                        appDatabase.musicDao.observeAllFromCollection(collection.collectionId)
                    }
                ) { musicLists ->
                    sortedCollections.mapIndexed { index, collection ->
                        CollectionWithMusics(
                            collection = collection.toCollection(),
                            musics = musicLists[index].map { it.toMusic() },
                        )
                    }
                }
            }
        }

    override fun getAllFromQuickAccess(): Flow<List<CollectionPreview>> =
        appDatabase.collectionDao.getAllFromQuickAccess().map { previews ->
            previews.map { it.toCollectionPreview() }
        }

    override suspend fun create(name: String): Collection =
        Collection(name = name.trim()).also {
            appDatabase.collectionDao.upsert(it.toRoomCollection())
        }

    override suspend fun upsertAll(collections: List<Collection>) {
        appDatabase.collectionDao.upsertAll(collections.map { it.toRoomCollection() })
    }

    override suspend fun deleteAll(collectionIds: List<Uuid>) {
        appDatabase.collectionDao.deleteAll(collectionIds)
    }

    override suspend fun addArtists(collectionIds: List<Uuid>, artistIds: List<Uuid>) {
        appDatabase.collectionDao.addArtists(
            collectionIds.flatMap { collectionId ->
                artistIds.map { artistId -> RoomCollectionArtist(collectionId, artistId) }
            }
        )
    }

    override suspend fun addAlbums(collectionIds: List<Uuid>, albumIds: List<Uuid>) {
        appDatabase.collectionDao.addAlbums(
            collectionIds.flatMap { collectionId ->
                albumIds.map { albumId -> RoomCollectionAlbum(collectionId, albumId) }
            }
        )
    }

    override suspend fun addPlaylists(collectionIds: List<Uuid>, playlistIds: List<Uuid>) {
        appDatabase.collectionDao.addPlaylists(
            collectionIds.flatMap { collectionId ->
                playlistIds.map { playlistId -> RoomCollectionPlaylist(collectionId, playlistId) }
            }
        )
    }

    override fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>> =
        appDatabase.collectionDao.getCollectionIdsContainingArtist(artistId)

    override fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>> =
        appDatabase.collectionDao.getCollectionIdsContainingAlbum(albumId)

    override fun getCollectionIdsContainingPlaylist(playlistId: Uuid): Flow<List<Uuid>> =
        appDatabase.collectionDao.getCollectionIdsContainingPlaylist(playlistId)

    override suspend fun incrementNbPlayed(collectionId: Uuid) {
        appDatabase.collectionDao.incrementNbPlayed(
            collectionId = collectionId,
            lastUpdatedMillis = DateUtils.now(),
        )
    }

    override suspend fun getAllRemoteToLocalIds(): Map<Uuid, Uuid> =
        appDatabase.collectionDao.getAllLocalToRemoteIds().associate {
            it.remoteId to it.localId
        }
}

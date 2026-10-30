package com.github.enteraname74.localdb.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.collection.RoomCollectionAlbum
import com.github.enteraname74.localdb.model.collection.RoomCollectionArtist
import com.github.enteraname74.localdb.model.collection.toCollection
import com.github.enteraname74.localdb.model.collection.toRoomCollection
import com.github.enteraname74.localdb.utils.PagingUtils
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionElementPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.CollectionSyncData
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import com.github.enteraname74.soulsearching.repository.datasource.collection.CollectionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlin.math.max
import kotlin.uuid.Uuid

internal class RoomCollectionLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
    private val settingsRepository: SettingsRepository,
) : CollectionLocalDataSource {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPaged(): Flow<PagingData<CollectionPreview>> =
        settingsRepository.observeSettings().flatMapLatest { settings ->
                val sortDirection = settings.collectionSortSetup.direction
                val sortType = settings.collectionSortSetup.type
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

    override fun getAll(): Flow<List<CollectionPreview>> =
        appDatabase.collectionDao.getAllPreviews().map { previews ->
            previews.map { it.toCollectionPreview() }
        }

    override fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?> =
        appDatabase.collectionDao.getCollectionPreview(collectionId).map {
            it?.toCollectionPreview()
        }

    override fun getElements(collectionId: Uuid): Flow<List<CollectionElementPreview>> = combine(
        appDatabase.collectionDao.getAlbumPreviews(collectionId),
        appDatabase.collectionDao.getArtistPreviews(collectionId),
    ) { albums, artists ->
        buildList {
            addAll(albums.map { CollectionElementPreview.Album(it.toAlbumPreview()) })
            addAll(artists.map { CollectionElementPreview.Artist(it.toArtistPreview()) })
        }.sortedWith(compareBy<CollectionElementPreview> { it.name }.thenBy { it.id.toString() })
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

    override fun searchAll(search: String): Flow<List<CollectionPreview>> =
        appDatabase.collectionDao.searchAll(search).map { previews ->
            previews.map { it.toCollectionPreview() }
        }

    override suspend fun create(name: String): Collection =
        Collection(name = name.trim()).also {
            appDatabase.collectionDao.upsert(it.toRoomCollection())
        }

    override suspend fun upsertAll(collections: List<Collection>, keepUpdatedAt: Boolean) {
        appDatabase.collectionDao.upsertAll(
            collections.map {
                it.copy(
                    lastUpdatedMillis = if (keepUpdatedAt) {
                        it.lastUpdatedMillis
                    } else {
                        max(DateUtils.now(), it.lastUpdatedMillis ?: 0L)
                    },
                ).toRoomCollection()
            }
        )
    }

    override suspend fun getFromRemoteId(remoteId: Uuid): Collection? =
        appDatabase.collectionDao.getFromRemoteId(remoteId)?.toCollection()

    override suspend fun getFromName(name: String): Collection? =
        appDatabase.collectionDao.getFromName(name)?.toCollection()

    override suspend fun deleteAll(collectionIds: List<Uuid>) {
        appDatabase.collectionDao.deleteAll(collectionIds)
    }

    override suspend fun addArtists(collectionIds: List<Uuid>, artistIds: List<Uuid>, keepUpdatedAt: Boolean) {
        if (collectionIds.isEmpty() || artistIds.isEmpty()) return
        appDatabase.collectionDao.addArtists(
            collectionIds.flatMap { collectionId ->
                artistIds.map { artistId -> RoomCollectionArtist(collectionId, artistId) }
            }
        )
        if (!keepUpdatedAt) touch(collectionIds)
    }

    override suspend fun addAlbums(collectionIds: List<Uuid>, albumIds: List<Uuid>, keepUpdatedAt: Boolean) {
        if (collectionIds.isEmpty() || albumIds.isEmpty()) return
        appDatabase.collectionDao.addAlbums(
            collectionIds.flatMap { collectionId ->
                albumIds.map { albumId -> RoomCollectionAlbum(collectionId, albumId) }
            }
        )
        if (!keepUpdatedAt) touch(collectionIds)
    }

    override suspend fun removeArtist(collectionId: Uuid, artistId: Uuid) {
        appDatabase.collectionDao.removeArtist(collectionId, artistId)
        touch(listOf(collectionId))
    }

    override suspend fun removeAlbum(collectionId: Uuid, albumId: Uuid) {
        appDatabase.collectionDao.removeAlbum(collectionId, albumId)
        touch(listOf(collectionId))
    }

    override fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>> =
        appDatabase.collectionDao.getCollectionIdsContainingArtist(artistId)

    override fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>> =
        appDatabase.collectionDao.getCollectionIdsContainingAlbum(albumId)

    override suspend fun incrementNbPlayed(collectionId: Uuid) {
        appDatabase.collectionDao.incrementNbPlayed(
            collectionId = collectionId,
            lastUpdatedMillis = DateUtils.now(),
        )
    }

    override suspend fun cleanAllCovers() {
        appDatabase.collectionDao.cleanAllCovers()
    }

    override suspend fun getAllRemoteToLocalIds(): Map<Uuid, Uuid> =
        appDatabase.collectionDao.getAllLocalToRemoteIds().associate {
            it.remoteId to it.localId
        }

    override suspend fun getAllToSendToCloud(): List<CollectionSyncData> =
        appDatabase.collectionDao.getAllToSendToCloud().map { roomCollection ->
            CollectionSyncData(
                collection = roomCollection.toCollection(),
                artistRemoteIds = appDatabase.collectionDao.getArtistRemoteIds(roomCollection.collectionId),
                albumRemoteIds = appDatabase.collectionDao.getAlbumRemoteIds(roomCollection.collectionId),
            )
        }

    override suspend fun getRemoteIdsFromIds(ids: List<Uuid>): List<Uuid> =
        if (ids.isEmpty()) emptyList() else appDatabase.collectionDao.getRemoteIdsFromIds(ids)

    override suspend fun getAllRemoteIdsPossessedByUser(): List<Uuid> =
        appDatabase.collectionDao.getAllRemoteIdsPossessedByUser()

    override suspend fun deleteAllFromRemote(remoteIds: List<Uuid>) {
        if (remoteIds.isNotEmpty()) appDatabase.collectionDao.deleteAllFromRemote(remoteIds)
    }

    override suspend fun deleteAllRemoteFields() {
        appDatabase.collectionDao.deleteAllRemoteFields()
    }

    override suspend fun deleteAllEmpty() {
        appDatabase.collectionDao.deleteAllEmpty()
    }

    override suspend fun getLatestUpdatedAt(): Long? =
        appDatabase.collectionDao.getLatestUpdatedAt()

    override suspend fun getLocalArtistIdsFromRemoteIds(remoteIds: List<Uuid>): List<Uuid> {
        if (remoteIds.isEmpty()) return emptyList()
        val mapping = appDatabase.artistDao.getAllRemoteToLocalIds().associate { it.remoteId to it.localId }
        return remoteIds.mapNotNull(mapping::get)
    }

    override suspend fun getLocalAlbumIdsFromRemoteIds(remoteIds: List<Uuid>): List<Uuid> {
        if (remoteIds.isEmpty()) return emptyList()
        val mapping = appDatabase.albumDao.getAllRemoteToLocalIds().associate { it.remoteId to it.localId }
        return remoteIds.mapNotNull(mapping::get)
    }

    private suspend fun touch(collectionIds: List<Uuid>) {
        if (collectionIds.isNotEmpty()) {
            appDatabase.collectionDao.touch(collectionIds, DateUtils.now())
        }
    }

    override fun getTotal(): Flow<Int> =
        appDatabase.collectionDao.getTotal()
}

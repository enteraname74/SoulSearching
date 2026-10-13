package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import androidx.paging.PagingSource
import com.github.enteraname74.localdb.model.collection.RoomCollection
import com.github.enteraname74.localdb.model.collection.RoomCollectionAlbum
import com.github.enteraname74.localdb.model.collection.RoomCollectionArtist
import com.github.enteraname74.localdb.model.mapping.GenericLocalIdToRemoteId
import com.github.enteraname74.localdb.view.RoomCollectionPreview
import com.github.enteraname74.localdb.view.RoomAlbumPreview
import com.github.enteraname74.localdb.view.RoomArtistPreview
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface CollectionDao {
    @Upsert
    suspend fun upsert(collection: RoomCollection)

    @Upsert
    suspend fun upsertAll(collections: List<RoomCollection>)

    @Query("SELECT * FROM RoomCollection")
    suspend fun getAllCollections(): List<RoomCollection>

    @Query("SELECT * FROM RoomCollectionPreview ORDER BY name ASC, id ASC")
    fun getAllPreviews(): Flow<List<RoomCollectionPreview>>

    @Query("SELECT * FROM RoomCollection WHERE collectionId IN (:collectionIds)")
    fun getFromIds(collectionIds: List<Uuid>): Flow<List<RoomCollection>>

    @Query("SELECT * FROM RoomCollectionPreview WHERE isInQuickAccess = 1")
    fun getAllFromQuickAccess(): Flow<List<RoomCollectionPreview>>

    @Query("SELECT * FROM RoomCollectionPreview ORDER BY name ASC, id ASC")
    fun getAllPagedByNameAsc(): PagingSource<Int, RoomCollectionPreview>

    @Query("SELECT * FROM RoomCollectionPreview ORDER BY name DESC, id ASC")
    fun getAllPagedByNameDesc(): PagingSource<Int, RoomCollectionPreview>

    @Query("SELECT * FROM RoomCollectionPreview ORDER BY addedDate ASC, id ASC")
    fun getAllPagedByDateAsc(): PagingSource<Int, RoomCollectionPreview>

    @Query("SELECT * FROM RoomCollectionPreview ORDER BY addedDate DESC, id ASC")
    fun getAllPagedByDateDesc(): PagingSource<Int, RoomCollectionPreview>

    @Query("SELECT * FROM RoomCollectionPreview ORDER BY nbPlayed ASC, id ASC")
    fun getAllPagedByNbPlayedAsc(): PagingSource<Int, RoomCollectionPreview>

    @Query("SELECT * FROM RoomCollectionPreview ORDER BY nbPlayed DESC, id ASC")
    fun getAllPagedByNbPlayedDesc(): PagingSource<Int, RoomCollectionPreview>

    @Upsert
    suspend fun addArtist(item: RoomCollectionArtist)

    @Upsert
    suspend fun addAlbum(item: RoomCollectionAlbum)

    @Upsert
    suspend fun addArtists(items: List<RoomCollectionArtist>)

    @Upsert
    suspend fun addAlbums(items: List<RoomCollectionAlbum>)

    @Query("SELECT collectionId FROM RoomCollectionArtist WHERE artistId = :artistId")
    fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>>

    @Query("SELECT collectionId FROM RoomCollectionAlbum WHERE albumId = :albumId")
    fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>>

    @Query("SELECT * FROM RoomCollectionPreview WHERE id = :collectionId LIMIT 1")
    fun getCollectionPreview(collectionId: Uuid): Flow<RoomCollectionPreview?>

    @Query(
        """
            SELECT preview.*
            FROM RoomAlbumPreview AS preview
            INNER JOIN RoomCollectionAlbum AS relation ON relation.albumId = preview.id
            WHERE relation.collectionId = :collectionId
            ORDER BY preview.name ASC, preview.id ASC
        """
    )
    fun getAlbumPreviews(collectionId: Uuid): Flow<List<RoomAlbumPreview>>

    @Query(
        """
            SELECT preview.*
            FROM RoomArtistPreview AS preview
            INNER JOIN RoomCollectionArtist AS relation ON relation.artistId = preview.id
            WHERE relation.collectionId = :collectionId
            ORDER BY preview.name ASC, preview.id ASC
        """
    )
    fun getArtistPreviews(collectionId: Uuid): Flow<List<RoomArtistPreview>>

    @Query(
        """
        UPDATE RoomCollection
        SET nbPlayed = nbPlayed + 1,
            lastUpdatedMillis = :lastUpdatedMillis
        WHERE collectionId = :collectionId
        """
    )
    suspend fun incrementNbPlayed(collectionId: Uuid, lastUpdatedMillis: Long)

    @Query("UPDATE RoomCollection SET coverId = NULL")
    suspend fun cleanAllCovers()

    @Query(
        """
        SELECT collectionId AS localId, remoteId
        FROM RoomCollection
        WHERE remoteId IS NOT NULL
        """
    )
    suspend fun getAllLocalToRemoteIds(): List<GenericLocalIdToRemoteId>

    @Query(
        """
        DELETE FROM RoomCollectionArtist
        WHERE collectionId = :collectionId AND artistId = :artistId
    """
    )
    suspend fun removeArtist(collectionId: Uuid, artistId: Uuid)

    @Query(
        """
        DELETE FROM RoomCollectionAlbum
        WHERE collectionId = :collectionId AND albumId = :albumId
    """
    )
    suspend fun removeAlbum(collectionId: Uuid, albumId: Uuid)

    @Query("UPDATE RoomCollection SET lastUpdatedMillis = :lastUpdatedMillis WHERE collectionId IN (:collectionIds)")
    suspend fun touch(collectionIds: List<Uuid>, lastUpdatedMillis: Long)

    @Query("DELETE FROM RoomCollection WHERE collectionId IN (:collectionIds)")
    suspend fun deleteAll(collectionIds: List<Uuid>)
}

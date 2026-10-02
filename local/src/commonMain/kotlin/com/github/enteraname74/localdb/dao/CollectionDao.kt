package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.collection.RoomCollection
import com.github.enteraname74.localdb.model.collection.RoomCollectionAlbum
import com.github.enteraname74.localdb.model.collection.RoomCollectionArtist
import com.github.enteraname74.localdb.model.collection.RoomCollectionPlaylist
import com.github.enteraname74.localdb.model.mapping.GenericLocalIdToRemoteId
import com.github.enteraname74.localdb.view.RoomCollectionPreview
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface CollectionDao {
    @Upsert
    suspend fun upsert(collection: RoomCollection)

    @Upsert
    suspend fun addArtist(item: RoomCollectionArtist)

    @Upsert
    suspend fun addAlbum(item: RoomCollectionAlbum)

    @Upsert
    suspend fun addPlaylist(item: RoomCollectionPlaylist)

    @Query("SELECT * FROM RoomCollectionPreview WHERE id = :collectionId LIMIT 1")
    fun getCollectionPreview(collectionId: Uuid): Flow<RoomCollectionPreview?>

    @Query(
        """
        UPDATE RoomCollection
        SET nbPlayed = nbPlayed + 1,
            lastUpdatedMillis = :lastUpdatedMillis
        WHERE collectionId = :collectionId
        """
    )
    suspend fun incrementNbPlayed(collectionId: Uuid, lastUpdatedMillis: Long)

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

    @Query(
        """
        DELETE FROM RoomCollectionPlaylist
        WHERE collectionId = :collectionId AND playlistId = :playlistId
    """
    )
    suspend fun removePlaylist(collectionId: Uuid, playlistId: Uuid)

    @Query("DELETE FROM RoomCollection WHERE collectionId = :collectionId")
    suspend fun delete(collectionId: Uuid)
}

package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomAlbum
import com.github.enteraname74.localdb.model.RoomAlbumArtist
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DAO of an AlbumArtist.
 */
@Dao
internal interface AlbumArtistDao {
    @Upsert
    suspend fun insertAlbumIntoArtist(roomAlbumArtist: RoomAlbumArtist)

    @Query("UPDATE RoomAlbumArtist SET artistId = :newArtistId WHERE albumId = :albumId")
    suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID)

    @Query("DELETE FROM RoomAlbumArtist WHERE albumId = :albumId")
    suspend fun deleteAlbumFromArtist(albumId: UUID)
}
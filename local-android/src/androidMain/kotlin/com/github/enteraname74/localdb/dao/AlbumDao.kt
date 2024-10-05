package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomAlbum
import com.github.enteraname74.localdb.model.RoomAlbumWithArtist
import com.github.enteraname74.localdb.model.RoomAlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DAO of an Album.
 */
@Dao
internal interface AlbumDao {

    @Upsert
    suspend fun upsert(roomAlbum: RoomAlbum)

    @Upsert
    suspend fun upsertAll(roomAlbums: List<RoomAlbum>)

    @Delete
    suspend fun delete(roomAlbum: RoomAlbum)

    @Query("DELETE FROM RoomAlbum WHERE albumId IN (:ids)")
    suspend fun deleteAll(ids: List<UUID>)

    @Query("SELECT * FROM RoomAlbum INNER JOIN RoomAlbumArtist WHERE RoomAlbum.albumId = RoomAlbumArtist.albumId AND RoomAlbumArtist.artistId = :artistId")
    fun getAllAlbumsFromArtist(artistId: UUID) : Flow<List<RoomAlbum>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum INNER JOIN RoomAlbumArtist WHERE RoomAlbum.albumId = RoomAlbumArtist.albumId AND RoomAlbumArtist.artistId = :artistId")
    fun getAllAlbumsWithMusicsFromArtist(artistId: UUID): Flow<List<RoomAlbumWithMusics>>

    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAll(): Flow<List<RoomAlbum>>

    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getFromId(albumId: UUID): Flow<RoomAlbum?>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getAlbumWithMusics(albumId: UUID): Flow<RoomAlbumWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAllAlbumWithMusics(): Flow<List<RoomAlbumWithMusics>>
}
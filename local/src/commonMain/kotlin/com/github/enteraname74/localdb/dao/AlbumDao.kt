package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.localdb.model.RoomAlbum
import com.github.enteraname74.localdb.model.RoomCompleteAlbum
import com.github.enteraname74.localdb.model.RoomCompleteAlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of an Album.
 */
@Dao
interface AlbumDao {

    @Upsert
    suspend fun upsert(roomAlbum: RoomAlbum)

    @Upsert
    suspend fun upsertAll(roomAlbums: List<RoomAlbum>)

    @Delete
    suspend fun delete(roomAlbum: RoomAlbum)

    @Query("DELETE FROM RoomAlbum WHERE albumId = :id")
    suspend fun delete(id: UUID)

    @Query("DELETE FROM RoomAlbum WHERE albumId IN (:ids)")
    suspend fun deleteAll(ids: List<UUID>)

    @Query("SELECT albumName FROM RoomAlbum WHERE LOWER(albumName) LIKE LOWER('%' || :search || '%')")
    suspend fun getAlbumNamesContainingSearch(search: String): List<String>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE artistId = :artistId")
    fun getAllAlbumsFromArtist(artistId: UUID) : Flow<List<RoomCompleteAlbum>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE artistId = :artistId")
    fun getAllAlbumsWithMusicsFromArtist(artistId: UUID): Flow<List<RoomCompleteAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAll(): Flow<List<RoomCompleteAlbum>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getFromId(albumId: UUID): Flow<RoomCompleteAlbum?>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getAlbumWithMusics(albumId: UUID): Flow<RoomCompleteAlbumWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAllAlbumWithMusics(): Flow<List<RoomCompleteAlbumWithMusics>>
}
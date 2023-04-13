package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface AlbumDao {

    @Upsert
    suspend fun insertAlbum(album: Album)

    @Delete
    suspend fun deleteAlbum(album : Album)

    @Query("SELECT * FROM Album ORDER BY name ASC")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM Album WHERE name = :name AND artist = :artist")
    fun getAlbumFromInfo(name : String, artist : String) : Album?

    @Transaction
    @Query("SELECT * FROM Album WHERE albumId = :albumId")
    fun getAlbumWithSongs(albumId : UUID): Flow<AlbumWithMusics>
}
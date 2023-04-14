package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithArtist
import com.github.soulsearching.database.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface AlbumDao {

    @Upsert
    suspend fun insertAlbum(album: Album)

    @Delete
    suspend fun deleteAlbum(album : Album)

    @Query("SELECT * FROM Album ORDER BY albumName ASC")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM Album WHERE albumId = :albumId")
    fun getAlbumFromId(albumId: UUID) : Album

    @Transaction
    @Query("SELECT * FROM Album WHERE albumId = :albumId")
    fun getAlbumWithMusics(albumId : UUID): Flow<AlbumWithMusics>

    @Transaction
    @Query("SELECT * FROM Album WHERE albumId = :albumId")
    fun getAlbumWithMusicsSimple(albumId : UUID): AlbumWithMusics

    @Transaction
    @Query("SELECT * FROM Album ORDER BY albumName ASC")
    fun getAllAlbumsWithArtist() : Flow<List<AlbumWithArtist>>

    @Transaction
    @Query("SELECT * FROM Album")
    fun getAllAlbumsWithArtistSimple() : List<AlbumWithArtist>

    @Transaction
    @Query("SELECT * FROM Album")
    fun getAllAlbumsWithMusicsSimple() : List<AlbumWithMusics>
}
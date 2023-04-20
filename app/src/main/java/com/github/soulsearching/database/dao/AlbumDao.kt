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
    suspend fun deleteAlbum(album: Album)

    @Query("SELECT * FROM Album INNER JOIN AlbumArtist WHERE Album.albumId = AlbumArtist.albumId AND AlbumArtist.artistId = :artistId")
    suspend fun getAllAlbumsFromArtist(artistId: UUID) : List<Album>

    @Query("SELECT * FROM Album ORDER BY albumName ASC")
    fun getAllAlbumsSortByNameAsc(): Flow<List<Album>>


    @Query("SELECT * FROM Album WHERE albumId = :albumId")
    fun getAlbumFromId(albumId: UUID): Album?

    @Transaction
    @Query("SELECT * FROM Album WHERE albumId = :albumId")
    fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?>

    @Transaction
    @Query("SELECT * FROM Album WHERE albumId = :albumId")
    fun getAlbumWithMusicsSimple(albumId: UUID): AlbumWithMusics

    @Transaction
    @Query("SELECT * FROM Album ORDER BY albumName ASC")
    fun getAllAlbumsWithArtistSortByNameAsc(): Flow<List<AlbumWithArtist>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY albumName DESC")
    fun getAllAlbumsWithArtistSortByNameDesc(): Flow<List<AlbumWithArtist>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY addedDate ASC")
    fun getAllAlbumsWithArtistSortByAddedDateAsc(): Flow<List<AlbumWithArtist>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY addedDate DESC")
    fun getAllAlbumsWithArtistSortByAddedDateDesc(): Flow<List<AlbumWithArtist>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY nbPlayed ASC")
    fun getAllAlbumsWithArtistSortByNbPlayedAsc(): Flow<List<AlbumWithArtist>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY nbPlayed DESC")
    fun getAllAlbumsWithArtistSortByNbPlayedDesc(): Flow<List<AlbumWithArtist>>

    @Transaction
    @Query("SELECT * FROM Album")
    fun getAllAlbumsWithArtistSimple(): List<AlbumWithArtist>

    @Transaction
    @Query("SELECT * FROM Album")
    fun getAllAlbumsWithMusicsSimple(): List<AlbumWithMusics>

    @Query(
        "SELECT Album.* FROM Album INNER JOIN AlbumArtist ON Album.albumId = AlbumArtist.albumId AND Album.albumName = :albumName AND AlbumArtist.artistId = :artistId"
    )
    fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album?

    @Query(
        "SELECT Album.* FROM Album INNER JOIN AlbumArtist ON Album.albumId = AlbumArtist.albumId AND Album.albumName = :albumName AND AlbumArtist.artistId = :artistId AND Album.albumId != :albumId"
    )
    fun getPossibleDuplicateAlbum(albumId: UUID, albumName: String, artistId: UUID): Album?
}
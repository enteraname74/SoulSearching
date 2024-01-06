package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.Album
import com.github.enteraname74.data.model.AlbumWithArtist
import com.github.enteraname74.data.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
internal interface AlbumDao {

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
    fun getAllAlbumsWithMusicsSortByNameAsc(): Flow<List<AlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY albumName DESC")
    fun getAllAlbumsWithMusicsSortByNameDesc(): Flow<List<AlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY addedDate ASC")
    fun getAllAlbumsWithMusicsSortByAddedDateAsc(): Flow<List<AlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY addedDate DESC")
    fun getAllAlbumsWithMusicsSortByAddedDateDesc(): Flow<List<AlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY nbPlayed ASC")
    fun getAllAlbumsWithMusicsSortByNbPlayedAsc(): Flow<List<AlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM Album ORDER BY nbPlayed DESC")
    fun getAllAlbumsWithMusicsSortByNbPlayedDesc(): Flow<List<AlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM Album")
    fun getAllAlbumsWithArtistSimple(): List<AlbumWithArtist>

    @Transaction
    @Query("SELECT * FROM Album")
    fun getAllAlbumsWithMusicsSimple(): List<AlbumWithMusics>

    @Transaction
    @Query("SELECT * FROM Album WHERE isInQuickAccess = 1")
    fun getAllAlbumsFromQuickAccess(): Flow<List<AlbumWithArtist>>

    @Query(
        "SELECT Album.* FROM Album INNER JOIN AlbumArtist ON Album.albumId = AlbumArtist.albumId AND Album.albumName = :albumName AND AlbumArtist.artistId = :artistId"
    )
    fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album?

    @Query(
        "SELECT Album.* FROM Album INNER JOIN AlbumArtist ON Album.albumId = AlbumArtist.albumId AND Album.albumName = :albumName AND AlbumArtist.artistId = :artistId AND Album.albumId != :albumId"
    )
    fun getPossibleDuplicateAlbum(albumId: UUID, albumName: String, artistId: UUID): Album?

    @Query("UPDATE Album SET coverId = :newCoverId WHERE albumId = :albumId")
    fun updateAlbumCover(newCoverId : UUID, albumId : UUID)

    @Query("UPDATE Album SET isInQuickAccess = :newQuickAccessState WHERE albumId = :albumId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID)

    @Query("SELECT COUNT(*) FROM Album WHERE coverId = :coverId")
    fun getNumberOfArtistsWithCoverId(coverId : UUID) : Int

    @Query("SELECT nbPlayed FROM Album WHERE albumId = :albumId")
    fun getNbPlayedOfAlbum(albumId: UUID): Int

    @Query("UPDATE Album SET nbPlayed = :newNbPlayed WHERE albumId = :albumId")
    fun updateNbPlayed(newNbPlayed: Int, albumId: UUID)
}
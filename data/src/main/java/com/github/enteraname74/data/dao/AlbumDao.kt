package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.RoomAlbum
import com.github.enteraname74.data.model.RoomAlbumWithArtist
import com.github.enteraname74.data.model.RoomAlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
internal interface AlbumDao {

    @Upsert
    suspend fun insertAlbum(roomAlbum: RoomAlbum)

    @Delete
    suspend fun deleteAlbum(roomAlbum: RoomAlbum)

    @Query("SELECT * FROM RoomAlbum INNER JOIN RoomAlbumArtist WHERE RoomAlbum.albumId = RoomAlbumArtist.albumId AND RoomAlbumArtist.artistId = :artistId")
    suspend fun getAllAlbumsFromArtist(artistId: UUID) : List<RoomAlbum>

    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAllAlbumsSortByNameAsc(): Flow<List<RoomAlbum>>

    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getAlbumFromId(albumId: UUID): RoomAlbum?

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getAlbumWithMusics(albumId: UUID): Flow<RoomAlbumWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getAlbumWithMusicsSimple(albumId: UUID): RoomAlbumWithMusics

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAllAlbumsWithMusicsSortByNameAsc(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName DESC")
    fun getAllAlbumsWithMusicsSortByNameDesc(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY addedDate ASC")
    fun getAllAlbumsWithMusicsSortByAddedDateAsc(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY addedDate DESC")
    fun getAllAlbumsWithMusicsSortByAddedDateDesc(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY nbPlayed ASC")
    fun getAllAlbumsWithMusicsSortByNbPlayedAsc(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY nbPlayed DESC")
    fun getAllAlbumsWithMusicsSortByNbPlayedDesc(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum")
    fun getAllAlbumsWithArtistSimple(): List<RoomAlbumWithArtist>

    @Transaction
    @Query("SELECT * FROM RoomAlbum")
    fun getAllAlbumsWithMusicsSimple(): List<RoomAlbumWithMusics>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE isInQuickAccess = 1")
    fun getAllAlbumsFromQuickAccess(): Flow<List<RoomAlbumWithArtist>>

    @Query(
        "SELECT RoomAlbum.* FROM RoomAlbum INNER JOIN RoomAlbumArtist ON RoomAlbum.albumId = RoomAlbumArtist.albumId AND RoomAlbum.albumName = :albumName AND RoomAlbumArtist.artistId = :artistId"
    )
    fun getCorrespondingAlbum(albumName: String, artistId: UUID): RoomAlbum?

    @Query(
        "SELECT RoomAlbum.* FROM RoomAlbum INNER JOIN RoomAlbumArtist ON RoomAlbum.albumId = RoomAlbumArtist.albumId AND RoomAlbum.albumName = :albumName AND RoomAlbumArtist.artistId = :artistId AND RoomAlbum.albumId != :albumId"
    )
    fun getPossibleDuplicateAlbum(albumId: UUID, albumName: String, artistId: UUID): RoomAlbum?

    @Query("UPDATE RoomAlbum SET coverId = :newCoverId WHERE albumId = :albumId")
    fun updateAlbumCover(newCoverId : UUID, albumId : UUID)

    @Query("UPDATE RoomAlbum SET isInQuickAccess = :newQuickAccessState WHERE albumId = :albumId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID)

    @Query("SELECT COUNT(*) FROM RoomAlbum WHERE coverId = :coverId")
    fun getNumberOfArtistsWithCoverId(coverId : UUID) : Int

    @Query("SELECT nbPlayed FROM RoomAlbum WHERE albumId = :albumId")
    fun getNbPlayedOfAlbum(albumId: UUID): Int

    @Query("UPDATE RoomAlbum SET nbPlayed = :newNbPlayed WHERE albumId = :albumId")
    fun updateNbPlayed(newNbPlayed: Int, albumId: UUID)
}
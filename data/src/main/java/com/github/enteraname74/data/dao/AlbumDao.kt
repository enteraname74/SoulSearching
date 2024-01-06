package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.RoomAlbum
import com.github.enteraname74.data.model.RoomAlbumWithArtist
import com.github.enteraname74.data.model.RoomAlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of an Album.
 */
@Dao
internal interface AlbumDao {

    @Upsert
    suspend fun insertAlbum(roomAlbum: RoomAlbum)

    @Delete
    suspend fun deleteAlbum(roomAlbum: RoomAlbum)

    @Query("SELECT * FROM RoomAlbum INNER JOIN RoomAlbumArtist WHERE RoomAlbum.albumId = RoomAlbumArtist.albumId AND RoomAlbumArtist.artistId = :artistId")
    suspend fun getAllAlbumsFromArtist(artistId: UUID) : List<RoomAlbum>

    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAllAlbumsSortByNameAscAsFlow(): Flow<List<RoomAlbum>>

    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    suspend fun getAlbumFromId(albumId: UUID): RoomAlbum?

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getAlbumWithMusicsAsFlow(albumId: UUID): Flow<RoomAlbumWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    suspend fun getAlbumWithMusics(albumId: UUID): RoomAlbumWithMusics

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAllAlbumsWithMusicsSortByNameAscAsFlow(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName DESC")
    fun getAllAlbumsWithMusicsSortByNameDescAsFlow(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY addedDate ASC")
    fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY addedDate DESC")
    fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY nbPlayed ASC")
    fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY nbPlayed DESC")
    fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<RoomAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum")
    suspend fun getAllAlbumsWithArtist(): List<RoomAlbumWithArtist>

    @Transaction
    @Query("SELECT * FROM RoomAlbum")
    suspend fun getAllAlbumsWithMusics(): List<RoomAlbumWithMusics>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE isInQuickAccess = 1")
    fun getAllAlbumWithArtistFromQuickAccessAsFlow(): Flow<List<RoomAlbumWithArtist>>

    @Query(
        "SELECT RoomAlbum.* FROM RoomAlbum INNER JOIN RoomAlbumArtist ON RoomAlbum.albumId = RoomAlbumArtist.albumId AND RoomAlbum.albumName = :albumName AND RoomAlbumArtist.artistId = :artistId"
    )
    suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID): RoomAlbum?

    @Query(
        "SELECT RoomAlbum.* FROM RoomAlbum INNER JOIN RoomAlbumArtist ON RoomAlbum.albumId = RoomAlbumArtist.albumId AND RoomAlbum.albumName = :albumName AND RoomAlbumArtist.artistId = :artistId AND RoomAlbum.albumId != :albumId"
    )
    suspend fun getPossibleDuplicateAlbum(albumId: UUID, albumName: String, artistId: UUID): RoomAlbum?

    @Query("UPDATE RoomAlbum SET coverId = :newCoverId WHERE albumId = :albumId")
    suspend fun updateAlbumCover(newCoverId : UUID, albumId : UUID)

    @Query("UPDATE RoomAlbum SET isInQuickAccess = :newQuickAccessState WHERE albumId = :albumId")
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID)

    @Query("SELECT COUNT(*) FROM RoomAlbum WHERE coverId = :coverId")
    suspend fun getNumberOfAlbumsWithCoverId(coverId : UUID) : Int

    @Query("SELECT nbPlayed FROM RoomAlbum WHERE albumId = :albumId")
    suspend fun getNbPlayedOfAlbum(albumId: UUID): Int

    @Query("UPDATE RoomAlbum SET nbPlayed = :newNbPlayed WHERE albumId = :albumId")
    suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID)
}
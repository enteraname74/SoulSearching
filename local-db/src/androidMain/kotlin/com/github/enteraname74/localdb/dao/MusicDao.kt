package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.localdb.model.RoomMusic
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of a Music.
 */
@Dao
internal interface MusicDao {
    @Upsert
    suspend fun insertMusic(roomMusic : RoomMusic)

    @Delete
    suspend fun deleteMusic(roomMusic : RoomMusic)

    @Query("DELETE FROM RoomMusic WHERE album = :album AND artist = :artist")
    suspend fun deleteMusicFromAlbum(album : String, artist : String)

    @Query("SELECT * FROM RoomMusic WHERE path = :musicPath")
    suspend fun getMusicFromPath(musicPath : String) : RoomMusic?

    @Query("SELECT * FROM RoomMusic WHERE musicId = :musicId LIMIT 1")
    suspend fun getMusicFromId(musicId : UUID): RoomMusic

    @Query("SELECT * FROM RoomMusic INNER JOIN RoomMusicPlaylist ON RoomMusic.musicId = RoomMusicPlaylist.musicId INNER JOIN RoomPlaylist ON RoomPlaylist.playlistId = RoomMusicPlaylist.playlistId WHERE RoomPlaylist.isFavorite = 1 AND RoomMusic.musicId = :musicId")
    suspend fun getMusicFromFavoritePlaylist(musicId: UUID): RoomMusic?

    @Query("SELECT path FROM RoomMusic")
    suspend fun getAllMusicsPaths(): List<String>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name ASC")
    fun getAllMusicsSortByNameAscAsFlow(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name DESC")
    fun getAllMusicsSortByNameDescAsFlow(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY addedDate ASC")
    fun getAllMusicsSortByAddedDateAscAsFlow(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY addedDate DESC")
    fun getAllMusicsSortByAddedDateDescAsFlow(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY nbPlayed ASC")
    fun getAllMusicsSortByNbPlayedAscAsFlow(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY nbPlayed DESC")
    fun getAllMusicsSortByNbPlayedDescAsFlow(): Flow<List<RoomMusic>>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isInQuickAccess = 1 AND isHidden = 0")
    fun getAllMusicsFromQuickAccessAsFlow(): Flow<List<RoomMusic>>

    @Query("SELECT RoomMusic.* FROM RoomMusic INNER JOIN RoomMusicAlbum WHERE RoomMusic.musicId = RoomMusicAlbum.musicId AND RoomMusicAlbum.albumId = :albumId AND RoomMusic.isHidden = 0")
    suspend fun getAllMusicFromAlbum(albumId : UUID) : List<RoomMusic>

    @Query("SELECT COUNT(*) FROM RoomMusic WHERE coverId = :coverId")
    suspend fun getNumberOfMusicsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE RoomMusic SET isInQuickAccess = :newQuickAccessState WHERE musicId = :musicId")
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID)

    @Query("SELECT nbPlayed FROM RoomMusic WHERE musicId = :musicId")
    suspend fun getNbPlayedOfMusic(musicId: UUID): Int

    @Query("UPDATE RoomMusic SET nbPlayed = :newNbPlayed WHERE musicId = :musicId")
    suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID)

    @Query("UPDATE RoomMusic SET isHidden = :newIsHidden WHERE folder = :folderName")
    suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean)

    @Query("SELECT * FROM RoomMusic WHERE folder = :folderName")
    suspend fun getMusicsFromFolder(folderName: String): List<RoomMusic>
}
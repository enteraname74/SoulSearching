package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.RoomMusic
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
internal interface MusicDao {
    @Upsert
    suspend fun insertMusic(roomMusic : RoomMusic)

    @Delete
    suspend fun deleteMusic(roomMusic : RoomMusic)

    @Query("DELETE FROM RoomMusic WHERE album = :album AND artist = :artist")
    suspend fun deleteMusicFromAlbum(album : String, artist : String)

    @Query("SELECT * FROM RoomMusic WHERE path = :musicPath")
    fun getMusicFromPath(musicPath : String) : RoomMusic?
    @Query("SELECT * FROM RoomMusic WHERE musicId = :musicId LIMIT 1")
    fun getMusicFromId(musicId : UUID): RoomMusic

    @Query("SELECT * FROM RoomMusic INNER JOIN RoomMusicPlaylist ON RoomMusic.musicId = RoomMusicPlaylist.musicId INNER JOIN RoomPlaylist ON RoomPlaylist.playlistId = RoomMusicPlaylist.playlistId WHERE RoomPlaylist.isFavorite = 1 AND RoomMusic.musicId = :musicId")
    suspend fun getMusicFromFavoritePlaylist(musicId: UUID): RoomMusic?

    @Query("SELECT path FROM RoomMusic")
    suspend fun getAllMusicsPaths(): List<String>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name ASC")
    fun getAllMusicsSortByNameAsc(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name DESC")
    fun getAllMusicsSortByNameDesc(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY addedDate ASC")
    fun getAllMusicsSortByAddedDateAsc(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY addedDate DESC")
    fun getAllMusicsSortByAddedDateDesc(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY nbPlayed ASC")
    fun getAllMusicsSortByNbPlayedAsc(): Flow<List<RoomMusic>>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY nbPlayed DESC")
    fun getAllMusicsSortByNbPlayedDesc(): Flow<List<RoomMusic>>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isInQuickAccess = 1 AND isHidden = 0")
    fun getAllMusicsFromQuickAccess(): Flow<List<RoomMusic>>

    @Query("SELECT RoomMusic.* FROM RoomMusic INNER JOIN RoomMusicAlbum WHERE RoomMusic.musicId = RoomMusicAlbum.musicId AND RoomMusicAlbum.albumId = :albumId AND RoomMusic.isHidden = 0")
    fun getMusicsFromAlbum(albumId : UUID) : List<RoomMusic>

    @Query("SELECT COUNT(*) FROM RoomMusic WHERE coverId = :coverId")
    fun getNumberOfMusicsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE RoomMusic SET isInQuickAccess = :newQuickAccessState WHERE musicId = :musicId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID)

    @Query("SELECT nbPlayed FROM RoomMusic WHERE musicId = :musicId")
    fun getNbPlayedOfMusic(musicId: UUID): Int

    @Query("UPDATE RoomMusic SET nbPlayed = :newNbPlayed WHERE musicId = :musicId")
    fun updateNbPlayed(newNbPlayed: Int, musicId: UUID)

    @Query("UPDATE RoomMusic SET isHidden = :newIsHidden WHERE folder = :folderName")
    fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean)

    @Query("SELECT * FROM RoomMusic WHERE folder = :folderName")
    suspend fun getMusicsFromFolder(folderName: String): List<RoomMusic>
}
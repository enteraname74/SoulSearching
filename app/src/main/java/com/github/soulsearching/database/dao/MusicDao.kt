package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.Music
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MusicDao {

    @Upsert
    suspend fun insertMusic(music : Music)

    @Delete
    suspend fun deleteMusic(music : Music)

    @Query("DELETE FROM Music WHERE album = :album AND artist = :artist")
    suspend fun deleteMusicFromAlbum(album : String, artist : String)

    @Query("SELECT * FROM Music WHERE path = :musicPath")
    fun getMusicFromPath(musicPath : String) : Music?
    @Query("SELECT * FROM Music WHERE musicId = :musicId LIMIT 1")
    fun getMusicFromId(musicId : UUID): Music

    @Query("SELECT * FROM Music INNER JOIN MusicPlaylist ON Music.musicId = MusicPlaylist.musicId INNER JOIN Playlist ON Playlist.playlistId = MusicPlaylist.playlistId WHERE Playlist.isFavorite = TRUE AND Music.musicId = :musicId")
    suspend fun getMusicFromFavoritePlaylist(musicId: UUID): Music?

    @Query("SELECT path FROM Music")
    suspend fun getAllMusicsPaths(): List<String>

    @Query("SELECT * FROM Music WHERE isHidden = FALSE ORDER BY name ASC")
    fun getAllMusicsSortByNameAsc(): Flow<List<Music>>

    @Query("SELECT * FROM Music WHERE isHidden = FALSE ORDER BY name DESC")
    fun getAllMusicsSortByNameDesc(): Flow<List<Music>>

    @Query("SELECT * FROM Music WHERE isHidden = FALSE ORDER BY addedDate ASC")
    fun getAllMusicsSortByAddedDateAsc(): Flow<List<Music>>

    @Query("SELECT * FROM Music WHERE isHidden = FALSE ORDER BY addedDate DESC")
    fun getAllMusicsSortByAddedDateDesc(): Flow<List<Music>>

    @Query("SELECT * FROM Music WHERE isHidden = FALSE ORDER BY nbPlayed ASC")
    fun getAllMusicsSortByNbPlayedAsc(): Flow<List<Music>>

    @Query("SELECT * FROM Music WHERE isHidden = FALSE ORDER BY nbPlayed DESC")
    fun getAllMusicsSortByNbPlayedDesc(): Flow<List<Music>>

    @Transaction
    @Query("SELECT * FROM Music WHERE isInQuickAccess = TRUE AND isHidden = FALSE")
    fun getAllMusicsFromQuickAccess(): Flow<List<Music>>

    @Query("SELECT Music.* FROM Music INNER JOIN MusicAlbum WHERE Music.musicId = MusicAlbum.musicId AND MusicAlbum.albumId = :albumId AND Music.isHidden = FALSE")
    fun getMusicsFromAlbum(albumId : UUID) : List<Music>

    @Query("SELECT COUNT(*) FROM Music WHERE coverId = :coverId")
    fun getNumberOfMusicsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE Music SET isInQuickAccess = :newQuickAccessState WHERE musicId = :musicId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID)

    @Query("SELECT nbPlayed FROM Music WHERE musicId = :musicId")
    fun getNbPlayedOfMusic(musicId: UUID): Int

    @Query("UPDATE Music SET nbPlayed = :newNbPlayed WHERE musicId = :musicId")
    fun updateNbPlayed(newNbPlayed: Int, musicId: UUID)

    @Query("UPDATE Music SET isHidden = :newIsHidden WHERE folder = :folderName")
    fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean)
}
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

    @Query("SELECT * FROM Music ORDER BY name ASC")
    fun getAllMusicsSortByNameAsc(): Flow<List<Music>>

    @Query("SELECT * FROM Music ORDER BY name DESC")
    fun getAllMusicsSortByNameDesc(): Flow<List<Music>>

    @Query("SELECT * FROM Music ORDER BY addedDate ASC")
    fun getAllMusicsSortByAddedDateAsc(): Flow<List<Music>>

    @Query("SELECT * FROM Music ORDER BY addedDate DESC")
    fun getAllMusicsSortByAddedDateDesc(): Flow<List<Music>>

    @Query("SELECT * FROM Music ORDER BY nbPlayed ASC")
    fun getAllMusicsSortByNbPlayedAsc(): Flow<List<Music>>

    @Query("SELECT * FROM Music ORDER BY nbPlayed DESC")
    fun getAllMusicsSortByNbPlayedDesc(): Flow<List<Music>>

    @Transaction
    @Query("SELECT * FROM Music WHERE isInQuickAccess = TRUE")
    fun getAllMusicsFromQuickAccess(): Flow<List<Music>>

    @Query("SELECT Music.* FROM Music INNER JOIN MusicAlbum WHERE Music.musicId = MusicAlbum.musicId AND MusicAlbum.albumId = :albumId")
    fun getMusicsFromAlbum(albumId : UUID) : List<Music>

    @Query("SELECT COUNT(*) FROM Music WHERE coverId = :coverId")
    fun getNumberOfMusicsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE Music SET isInQuickAccess = :newQuickAccessState WHERE musicId = :musicId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID)
}
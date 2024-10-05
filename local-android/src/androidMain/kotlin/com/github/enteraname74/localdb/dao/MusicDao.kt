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
    suspend fun upsert(roomMusic : RoomMusic)

    @Upsert
    suspend fun upsertAll(roomMusics : List<RoomMusic>)

    @Delete
    suspend fun delete(roomMusic : RoomMusic)

    @Query("DELETE FROM RoomMusic WHERE musicId IN (:ids)")
    suspend fun deleteAll(ids: List<UUID>)

    @Query("DELETE FROM RoomMusic WHERE album = :album AND artist = :artist")
    suspend fun deleteMusicFromAlbum(album : String, artist : String)

    @Query("SELECT * FROM RoomMusic WHERE path = :musicPath")
    suspend fun getMusicFromPath(musicPath : String) : RoomMusic?

    @Query("SELECT * FROM RoomMusic WHERE musicId = :musicId LIMIT 1")
    fun getFromId(musicId : UUID): Flow<RoomMusic?>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name ASC")
    fun getAll(): Flow<List<RoomMusic>>

    @Query("SELECT RoomMusic.* FROM RoomMusic INNER JOIN RoomMusicAlbum WHERE RoomMusic.musicId = RoomMusicAlbum.musicId AND RoomMusicAlbum.albumId = :albumId AND RoomMusic.isHidden = 0")
    suspend fun getAllMusicFromAlbum(albumId : UUID) : List<RoomMusic>
}
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

    @Query("UPDATE RoomMusic SET albumId = :newAlbumId WHERE albumId = :legacyAlbumId")
    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID)

    @Query("DELETE FROM RoomMusic WHERE musicId IN (:ids)")
    suspend fun deleteAll(ids: List<UUID>)

    @Query("DELETE FROM RoomMusic WHERE dataMode = :dataMode")
    suspend fun deleteAll(dataMode: String)

    @Query("SELECT * FROM RoomMusic WHERE path = :musicPath")
    suspend fun getMusicFromPath(musicPath : String) : RoomMusic?

    @Query("SELECT * FROM RoomMusic WHERE musicId = :musicId LIMIT 1")
    fun getFromId(musicId : UUID): Flow<RoomMusic?>

    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND dataMode = :dataMode ORDER BY name ASC")
    fun getAll(dataMode: String): Flow<List<RoomMusic>>

    @Query("SELECT RoomMusic.* FROM RoomMusic WHERE albumId = :albumId AND isHidden = 0")
    suspend fun getAllMusicFromAlbum(albumId : UUID) : List<RoomMusic>
}
package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomCompleteMusic
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

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE musicId = :musicId LIMIT 1")
    fun getFromId(musicId : UUID): Flow<RoomCompleteMusic?>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name ASC")
    fun getAll(): Flow<List<RoomCompleteMusic>>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE musicId IN (:ids)")
    fun getAllFromId(ids: List<UUID>): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE albumId = :albumId AND RoomMusic.isHidden = 0")
    suspend fun getAllMusicFromAlbum(albumId : UUID) : List<RoomCompleteMusic>

    @Query("UPDATE RoomMusic SET albumId = :newAlbumId WHERE albumId = :legacyAlbumId")
    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID)
}
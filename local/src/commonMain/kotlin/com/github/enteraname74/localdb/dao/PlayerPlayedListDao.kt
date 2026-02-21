package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.localdb.model.player.RoomPlayerPlayedList
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PlayerPlayedListDao {

    @Query(
        """
            SELECT * FROM RoomPlayerPlayedList 
            WHERE state != "Cached"
            LIMIT 1
        """
    )
    fun getCurrentPlayedList(): Flow<RoomPlayerPlayedList?>

    @Query("SELECT * FROM RoomPlayerPlayedList")
    suspend fun getAll(): List<RoomPlayerPlayedList>

    @Query(
        """
            SELECT mode FROM RoomPlayerPlayedList 
            WHERE state != "Cached"
            LIMIT 1
        """
    )
    fun getCurrentMode(): Flow<PlayerMode?>

    @Query(
        """
            SELECT state FROM RoomPlayerPlayedList 
            WHERE state != "Cached"
            LIMIT 1
        """
    )
    fun getCurrentState(): Flow<PlayedListState?>

    @Query(
        """
            UPDATE RoomPlayerPlayedList 
            SET state = "Cached"
        """
    )
    suspend fun cacheAll()

    @Query(
        """
            UPDATE RoomPlayerPlayedList 
            SET mode = :mode 
            WHERE state != "Cached"
        """
    )
    suspend fun setMode(mode: PlayerMode)

    @Query(
        """
            UPDATE RoomPlayerPlayedList 
            SET state = :state 
            WHERE state != "Cached"
        """
    )
    suspend fun setState(state: PlayedListState)

    @Upsert
    suspend fun upsert(playedList: RoomPlayerPlayedList)

    @Query("DELETE FROM RoomPlayerPlayedList WHERE id = :playedListId")
    suspend fun delete(playedListId: UUID)

    @Query(
        """
            DELETE FROM RoomPlayerPlayedList 
            WHERE state != "Cached"
        """
    )
    suspend fun deleteCurrent()

    @Query(
        """
            DELETE FROM RoomPlayerPlayedList 
            WHERE playlistId IS NULL
        """
    )
    suspend fun deleteMainAndSearch()

    @Query(
        """
            DELETE FROM RoomPlayerPlayedList 
            WHERE playlistId = :playlistId
        """
    )
    suspend fun deletePlaylist(playlistId: UUID)
}
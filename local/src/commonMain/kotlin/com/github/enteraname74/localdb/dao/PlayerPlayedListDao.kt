package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.localdb.model.player.RoomPlayerPlayedList
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface PlayerPlayedListDao {

    @Query(
        """
            SELECT * FROM RoomPlayerPlayedList 
            WHERE state != 'Cached'
            LIMIT 1
        """
    )
    fun getCurrentPlayedList(): Flow<RoomPlayerPlayedList?>

    @Query(
        """
            SELECT * FROM RoomPlayerPlayedList 
            WHERE state = 'Cached' 
            AND playlistId = :playlistId
            LIMIT 1
        """
    )
    fun getCachedPlayedList(playlistId: String): Flow<RoomPlayerPlayedList?>

    @Query("SELECT * FROM RoomPlayerPlayedList")
    suspend fun getAll(): List<RoomPlayerPlayedList>

    @Query(
        """
            SELECT mode FROM RoomPlayerPlayedList 
            WHERE state != 'Cached'
            LIMIT 1
        """
    )
    fun getCurrentMode(): Flow<PlayerMode?>

    @Query(
        """
            SELECT state FROM RoomPlayerPlayedList 
            WHERE state != 'Cached'
            LIMIT 1
        """
    )
    fun getCurrentState(): Flow<PlayedListState?>

    @Query(
        """
            SELECT scope FROM RoomPlayerPlayedList 
            WHERE state != 'Cached'
            LIMIT 1
        """
    )
    fun getCurrentScope(): Flow<PlayedListScope?>

    @Query(
        """
            UPDATE RoomPlayerPlayedList 
            SET state = 'Cached'
        """
    )
    suspend fun cacheAll()

    @Query(
        """
            UPDATE RoomPlayerPlayedList 
            SET mode = :mode 
            WHERE state != 'Cached'
        """
    )
    suspend fun setMode(mode: PlayerMode)

    @Query(
        """
            UPDATE RoomPlayerPlayedList 
            SET state = :state 
            WHERE state != 'Cached'
        """
    )
    suspend fun setState(state: PlayedListState)

    @Query(
        """
            UPDATE RoomPlayerPlayedList 
            SET scope = :scope 
            WHERE state != 'Cached'
        """
    )
    suspend fun setScope(scope: PlayedListScope)

    @Query(
        """
            UPDATE RoomPlayerPlayedList 
            SET state = :state 
            WHERE id = :playedListId
        """
    )
    suspend fun setStateOfId(
        state: PlayedListState,
        playedListId: Uuid,
    )

    @Upsert
    suspend fun upsert(playedList: RoomPlayerPlayedList)

    @Query("DELETE FROM RoomPlayerPlayedList WHERE id = :playedListId")
    suspend fun delete(playedListId: Uuid)

    @Query(
        """
            DELETE FROM RoomPlayerPlayedList 
            WHERE state != 'Cached'
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
    suspend fun deletePlaylist(playlistId: String)
}

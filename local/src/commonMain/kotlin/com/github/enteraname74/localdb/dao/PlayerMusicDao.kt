package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.player.RoomCompletePlayerMusic
import com.github.enteraname74.localdb.model.player.RoomPlayerMusic
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * DAO of a PlayerMusic
 */
@Dao
interface PlayerMusicDao {

    @Transaction
    @Query(
        "SELECT * FROM CurrentPlayerMusicsView"
    )
    suspend fun getAll(): List<RoomPlayerMusic>

    @Transaction
    @Query(
        "SELECT * FROM CurrentPlayerMusicsView"
    )
    fun getAllAsFlow(): Flow<List<RoomCompletePlayerMusic>>

    @Transaction
    @Query(
        "SELECT COUNT(*) FROM CurrentPlayerMusicsView"
    )
    fun getSize(): Flow<Int>

    @Transaction
    @Query(
        """
            SELECT * FROM CurrentPlayerMusicsView
        """
    )
    fun getAllPaginated(): PagingSource<Int, RoomCompletePlayerMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM CurrentPlayerMusicsView 
            ORDER BY lastPlayedMillis DESC 
            LIMIT 1
        """
    )
    fun getCurrentMusic(): Flow<RoomCompletePlayerMusic?>

    @Query(
        """
            SELECT * FROM CurrentPlayerMusicsView 
            WHERE musicId = :musicId 
            LIMIT 1
        """
    )
    suspend fun getOfCurrentList(musicId: Uuid): RoomPlayerMusic?

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlayerMusic  
            WHERE playedListId = :playedListId
            ORDER BY lastPlayedMillis DESC 
            LIMIT 1
        """
    )
    fun getCurrentOfPlayedList(
        playedListId: Uuid,
    ): Flow<RoomCompletePlayerMusic?>

    @Transaction
    @Query(
        """
            SELECT * FROM CurrentPlayerMusicsView
            ORDER BY lastPlayedMillis DESC 
            LIMIT 1
        """
    )
    fun getCurrentMusicPaged(): PagingSource<Int, RoomCompletePlayerMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM CurrentPlayerMusicsView 
            WHERE currentOrder > :order 
            AND musicId NOT IN (:musicIdsToSkip)
            LIMIT 1
        """
    )
    fun getNextMusic(
        order: Double,
        musicIdsToSkip: List<Uuid>,
    ): Flow<RoomCompletePlayerMusic?>

    @Transaction
    @Query(
        """
            SELECT * FROM CurrentPlayerMusicsView 
            WHERE currentOrder < :order 
            ORDER BY currentOrder DESC
            LIMIT 1
        """
    )
    fun getPreviousMusic(order: Double): Flow<RoomCompletePlayerMusic?>

    @Transaction
    @Query(
        """
            SELECT * FROM CurrentPlayerMusicsView 
            ORDER BY currentOrder DESC
            LIMIT 1
        """
    )
    fun getLast(): Flow<RoomCompletePlayerMusic?>

    @Transaction
    @Query(
        """
            SELECT * FROM CurrentPlayerMusicsView 
            WHERE musicId NOT IN (:musicIdsToSkip)
            LIMIT 1
        """
    )
    fun getFirst(
        musicIdsToSkip: List<Uuid>
    ): Flow<RoomCompletePlayerMusic?>

    @Upsert
    suspend fun upsertAll(playerMusics: List<RoomPlayerMusic>)

    @Update
    suspend fun updateAll(playerMusics: List<RoomPlayerMusic>)

    @Query(
        """
            DELETE FROM RoomPlayerMusic 
            WHERE musicId = :musicId 
            AND playedListId IN (SELECT playedListId FROM CurrentPlayerMusicsView LIMIT 1)
        """
    )
    suspend fun delete(musicId: Uuid)

    @Query(
        """
            DELETE FROM RoomPlayerMusic 
            WHERE musicId IN (:musicIds)
            AND playedListId IN (SELECT playedListId FROM CurrentPlayerMusicsView LIMIT 1)
        """
    )
    suspend fun deleteAll(musicIds: List<Uuid>)

    @Query(
        """
            DELETE FROM RoomPlayerMusic 
            WHERE musicId NOT IN (:musicIds)
            AND playedListId IN (SELECT playedListId FROM CurrentPlayerMusicsView LIMIT 1)
        """
    )
    suspend fun deleteAllExpect(musicIds: List<Uuid>)

    @Query(
        """
            UPDATE RoomPlayerMusic 
            SET `order` = shuffledOrder
            WHERE playedListId IN (SELECT playedListId FROM CurrentPlayerMusicsView LIMIT 1)
        """
    )
    suspend fun switchToNormalWithShuffleOrder()

    @Query(
        """
            UPDATE RoomPlayerMusic 
            SET lastPlayedMillis = :lastPlayedMillis
            WHERE playedListId IN (SELECT playedListId FROM CurrentPlayerMusicsView LIMIT 1) 
            AND musicId = :musicId
        """
    )
    suspend fun setCurrent(
        musicId: Uuid,
        lastPlayedMillis: Long,
    )

    @Query(
        """
            SELECT COUNT(*) + 1 AS position
            FROM CurrentPlayerMusicsView
            WHERE currentOrder < (
                SELECT currentOrder
                FROM CurrentPlayerMusicsView
                WHERE musicId = :musicId
            )
        """
    )
    fun getPositionInList(musicId: Uuid): Flow<Int>
}

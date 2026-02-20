package com.github.enteraname74.localdb.view

import androidx.room.DatabaseView
import com.github.enteraname74.domain.model.player.PlayerMode
import java.util.UUID

@DatabaseView(
    """
    WITH currentPlayedList AS (
        SELECT *
        FROM RoomPlayerPlayedList  
        ORDER BY state != 'Cached'
        LIMIT 1
    )
    SELECT 
        m.*,
        CASE
            WHEN c.mode = 'Shuffle'
            THEN m.shuffledOrder
            ELSE m.`order`
        END AS currentOrder,
        c.mode 
    FROM RoomPlayerMusic m
    INNER JOIN currentPlayedList c ON m.playedListId = c.id
    ORDER BY currentOrder
    """
)
data class CurrentPlayerMusicsView(
    val musicId: UUID,
    val playedListId: UUID,
    val order: Double,
    val shuffledOrder: Double,
    val lastPlayedMillis: Long?,
    val currentOrder: Double,
    val mode: PlayerMode,
)

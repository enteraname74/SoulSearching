package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.localdb.model.RoomMusic
import java.util.*

/**
 * Room representation of a PlayerMusic.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomMusic::class,
            parentColumns = ["musicId"],
            childColumns = ["musicId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomPlayerPlayedList::class,
            parentColumns = ["id"],
            childColumns = ["playedListId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playedListId"), Index("musicId")],
)
data class RoomPlayerMusic(
    @PrimaryKey
    val id: String,
    val musicId: UUID,
    val playedListId: UUID,
    val order: Double,
    val shuffledOrder: Double,
    val lastPlayedMillis: Long?,
) {
    fun usedOrder(mode: PlayerMode): Double =
        if (mode == PlayerMode.Shuffle) {
            shuffledOrder
        } else {
            order
        }

    fun updateUsedOrder(
        mode: PlayerMode,
        newOrder: Double,
    ): RoomPlayerMusic =
        if (mode == PlayerMode.Shuffle) {
            copy(shuffledOrder = newOrder)
        } else {
            copy(order = newOrder)
        }
}

internal fun PlayerMusic.toRoomPlayerMusic(): RoomPlayerMusic =
    RoomPlayerMusic(
        musicId = music.musicId,
        playedListId = playedListId,
        order = order,
        shuffledOrder = shuffledOrder,
        lastPlayedMillis = lastPlayedMillis,
        id = "${music.musicId}-$playedListId"
    )
package com.github.enteraname74.localdb.model.player

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.localdb.model.RoomMusic
import kotlin.uuid.Uuid

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
    val musicId: Uuid,
    val playedListId: Uuid,
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

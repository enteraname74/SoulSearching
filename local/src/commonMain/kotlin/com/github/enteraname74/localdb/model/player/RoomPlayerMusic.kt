package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.localdb.model.RoomMusic
import java.util.*

/**
 * Room representation of a PlayerMusic.
 */
@Entity(
    primaryKeys = ["musicId", "playedListId"],
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
    indices = [Index("playedListId")],
)
data class RoomPlayerMusic(
    val musicId: UUID,
    val playedListId: UUID,
    val order: Double,
    val shuffledOrder: Double,
    val lastPlayedMillis: Long?,
)

internal fun PlayerMusic.toRoomPlayerMusic(): RoomPlayerMusic =
    RoomPlayerMusic(
        musicId = music.musicId,
        playedListId = playedListId,
        order = order,
        shuffledOrder = shuffledOrder,
        lastPlayedMillis = lastPlayedMillis,
    )
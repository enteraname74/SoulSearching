package com.github.enteraname74.localandroid.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.PlayerMusic
import java.util.*

/**
 * Room representation of a PlayerMusic.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomMusic::class,
            parentColumns = ["musicId"],
            childColumns = ["playerMusicId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class RoomPlayerMusic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val playerMusicId: UUID = UUID.randomUUID()
)

/**
 * Converts a RoomPlayerMusic to a PlayerMusic.
 */
internal fun RoomPlayerMusic.toPlayerMusic(): PlayerMusic = PlayerMusic(
    id = id,
    playerMusicId = playerMusicId
)

/**
 * Converts a PlayerMusic to a RoomPlayerMusic.
 */
internal fun PlayerMusic.toRoomPlayerMusic(): RoomPlayerMusic = RoomPlayerMusic(
    id = id,
    playerMusicId = playerMusicId
)
package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomPlayerMusic::class,
            parentColumns = ["id"],
            childColumns = ["playerMusicId"],
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
data class RoomPlayerMusicProgress(
    @PrimaryKey
    val playedListId: UUID,
    val playerMusicId: String,
    val progress: Int,
)

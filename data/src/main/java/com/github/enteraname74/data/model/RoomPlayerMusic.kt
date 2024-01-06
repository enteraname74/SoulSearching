package com.github.enteraname74.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

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
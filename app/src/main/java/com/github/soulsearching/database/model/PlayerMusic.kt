package com.github.soulsearching.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Music::class,
            parentColumns = ["musicId"],
            childColumns = ["playerMusicId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlayerMusic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val playerMusicId: UUID = UUID.randomUUID()
)
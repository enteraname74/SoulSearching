package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class RoomMusicWithCover(
    @Embedded val roomMusic : RoomMusic = RoomMusic(),
    @Relation(
            parentColumn = "musicId",
            entityColumn = "coverId",
            associateBy = Junction(RoomImageCover::class)
    )
    val cover : RoomImageCover? = null
)
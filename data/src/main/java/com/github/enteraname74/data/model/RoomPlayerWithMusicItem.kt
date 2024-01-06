package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Relation

internal data class RoomPlayerWithMusicItem(
    @Embedded val roomPlayerMusic: RoomPlayerMusic = RoomPlayerMusic(),
    @Relation(
        parentColumn = "playerMusicId",
        entityColumn = "musicId",
    )
    val roomMusic: RoomMusic? = null
)
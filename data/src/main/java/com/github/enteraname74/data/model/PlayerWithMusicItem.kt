package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class PlayerWithMusicItem(
    @Embedded val playerMusic: PlayerMusic = PlayerMusic(),
    @Relation(
        parentColumn = "playerMusicId",
        entityColumn = "musicId",
    )
    val music: Music? = null
)
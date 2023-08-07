package com.github.soulsearching.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlayerWithMusicItem(
    @Embedded val playerMusic: PlayerMusic = PlayerMusic(),
    @Relation(
        parentColumn = "playerMusicId",
        entityColumn = "musicId",
    )
    val music: Music? = null
)
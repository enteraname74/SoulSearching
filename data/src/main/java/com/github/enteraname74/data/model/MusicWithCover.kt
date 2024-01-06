package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class MusicWithCover(
    @Embedded val music : Music = Music(),
    @Relation(
            parentColumn = "musicId",
            entityColumn = "coverId",
            associateBy = Junction(ImageCover::class)
    )
    val cover : ImageCover? = null
)
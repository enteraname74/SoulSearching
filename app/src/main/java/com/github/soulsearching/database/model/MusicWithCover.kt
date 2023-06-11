package com.github.soulsearching.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MusicWithCover(
    @Embedded val music : Music = Music(),
    @Relation(
            parentColumn = "musicId",
            entityColumn = "coverId",
            associateBy = Junction(ImageCover::class)
    )
    val cover : ImageCover? = null
)
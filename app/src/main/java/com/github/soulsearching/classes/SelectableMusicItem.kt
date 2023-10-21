package com.github.soulsearching.classes

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Music

data class SelectableMusicItem(
    val music: Music,
    val cover: Bitmap?,
    var isSelected: Boolean
)

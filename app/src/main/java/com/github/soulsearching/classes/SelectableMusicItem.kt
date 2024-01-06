package com.github.soulsearching.classes

import android.graphics.Bitmap
import com.github.enteraname74.domain.model.Music

/**
 * Define information about a song, its cover and if it is selected.
 */
data class SelectableMusicItem(
    val music: Music,
    val cover: Bitmap?,
    var isSelected: Boolean
)

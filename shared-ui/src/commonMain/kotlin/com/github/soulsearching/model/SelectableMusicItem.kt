package com.github.soulsearching.model

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music

/**
 * Define information about a song, its cover and if it is selected.
 */
data class SelectableMusicItem(
    val music: Music,
    val cover: ImageBitmap?,
    var isSelected: Boolean
)

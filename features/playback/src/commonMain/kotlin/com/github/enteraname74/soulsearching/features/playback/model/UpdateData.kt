package com.github.enteraname74.soulsearching.features.playback.model

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music

data class UpdateData(
    val cover: ImageBitmap?,
    val music: Music,
    val position: Long,
    val playedListSize: Long,
    val isPlaying: Boolean,
    val isInFavorite: Boolean,
)

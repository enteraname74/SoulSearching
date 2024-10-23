package com.github.enteraname74.soulsearching.features.playback.notification

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState

interface SoulSearchingNotification {
    suspend fun updateNotification(
        playbackManagerState: PlaybackManagerState.Data,
        cover: ImageBitmap?,
    )
    fun dismissNotification()
}
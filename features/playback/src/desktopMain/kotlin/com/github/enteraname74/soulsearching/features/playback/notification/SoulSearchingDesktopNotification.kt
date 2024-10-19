package com.github.enteraname74.soulsearching.features.playback.notification

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState

class SoulSearchingDesktopNotification: SoulSearchingNotification {
    override suspend fun updateNotification(playbackManagerState: PlaybackManagerState.Data, cover: ImageBitmap?) {
        /*no-op*/
    }

    override fun dismissNotification() {
        /*no-op*/
    }
}
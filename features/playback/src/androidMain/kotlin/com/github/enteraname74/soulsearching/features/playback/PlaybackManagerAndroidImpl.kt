package com.github.enteraname74.soulsearching.features.playback

import android.content.Context
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingAndroidPlayerImpl
import org.koin.core.component.inject

class PlaybackManagerAndroidImpl(
    private val context: Context
): PlaybackManager() {
    override val player: SoulSearchingPlayer by lazy {
        SoulSearchingAndroidPlayerImpl(
            context = context,
            playbackManager = this,
        )
    }

    override val notification: SoulSearchingNotification by inject()
}
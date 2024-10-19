package com.github.enteraname74.soulsearching.features.playback

import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingDesktopPlayerImpl
import org.koin.core.component.inject

class PlaybackManagerDesktopImpl: PlaybackManager() {
    override val player: SoulSearchingPlayer by lazy {
        SoulSearchingDesktopPlayerImpl(
            playbackManager = this,
        )
    }
    override val notification: SoulSearchingNotification by inject()
}
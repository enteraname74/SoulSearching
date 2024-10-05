package com.github.enteraname74.soulsearching.model

import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Class managing playback on desktop.
 */
class PlaybackManagerDesktopImpl(): PlaybackManager(), KoinComponent {
    override val player: SoulSearchingDesktopPlayerImpl by inject()

    override fun stopPlayback(resetPlayedList: Boolean) {
        if (shouldInit) return
        player.dismiss()
        shouldInit = true
        super.stopPlayback(resetPlayedList)
    }

    override fun updateNotification() {}
}
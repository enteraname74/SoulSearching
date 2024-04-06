package com.github.soulsearching.model

import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.model.settings.SoulSearchingSettings

/**
 * Class managing playback on desktop.
 */
class PlaybackManagerDesktopImpl(
    settings: SoulSearchingSettings
): PlaybackManager(
    settings = settings
) {
    override val player: SoulSearchingPlayer
        get() = TODO("Not yet implemented")

    override fun stopPlayback() {
        TODO("Not yet implemented")
    }

    override fun update() {
        TODO("Not yet implemented")
    }
}
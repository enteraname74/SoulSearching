package com.github.soulsearching.model

import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.player.domain.model.SoulSearchingPlayer

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
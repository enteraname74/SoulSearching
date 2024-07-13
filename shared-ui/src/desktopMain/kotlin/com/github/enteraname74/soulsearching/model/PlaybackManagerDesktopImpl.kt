package com.github.enteraname74.soulsearching.model

import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerMusicRepository
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.SoulSearchingPlayer

/**
 * Class managing playback on desktop.
 */
class PlaybackManagerDesktopImpl(
    settings: SoulSearchingSettings,
    playerMusicRepository: PlayerMusicRepository,
    musicRepository: MusicRepository,
): PlaybackManager(
    settings = settings,
    playerMusicRepository = playerMusicRepository,
    musicRepository = musicRepository
) {
    override val player: SoulSearchingPlayer
        get() = SoulSearchingDesktopPlayerImpl()

    override fun stopPlayback(resetPlayedList: Boolean) {

    }

    override fun updateNotification() {}

    override fun update() {

    }
}
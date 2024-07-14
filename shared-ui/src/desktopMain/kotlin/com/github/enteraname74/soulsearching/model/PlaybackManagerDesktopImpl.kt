package com.github.enteraname74.soulsearching.model

import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerMusicRepository
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
), KoinComponent {
    override val player: SoulSearchingDesktopPlayerImpl by inject()

    override fun stopPlayback(resetPlayedList: Boolean) {
        if (shouldInit) return
        player.dismiss()
        shouldInit = true
        super.stopPlayback(resetPlayedList)
    }

    override fun updateNotification() {}
}
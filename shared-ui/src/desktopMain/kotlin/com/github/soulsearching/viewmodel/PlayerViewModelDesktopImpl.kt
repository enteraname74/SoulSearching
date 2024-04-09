package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.player.domain.PlayerViewModelHandler

/**
 * Implementation of the PlayerViewModel.
 */
class PlayerViewModelDesktopImpl(
    musicRepository: MusicRepository,
    playbackManager: PlaybackManagerDesktopImpl,
    colorThemeManager: ColorThemeManager
): PlayerViewModel {
    override val handler: PlayerViewModelHandler = PlayerViewModelHandler(
        musicRepository = musicRepository,
        playbackManager = playbackManager,
        colorThemeManager = colorThemeManager
    )
}
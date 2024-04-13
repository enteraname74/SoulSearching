package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.util.LyricsProvider
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
    colorThemeManager: ColorThemeManager,
    lyricsProvider: LyricsProvider,
    musicPlaylistRepository: MusicPlaylistRepository,
    playlistRepository: PlaylistRepository
): PlayerViewModel {
    override val handler: PlayerViewModelHandler = PlayerViewModelHandler(
        musicRepository = musicRepository,
        playbackManager = playbackManager,
        colorThemeManager = colorThemeManager,
        lyricsProvider = lyricsProvider,
        musicPlaylistRepository = musicPlaylistRepository,
        playlistRepository = playlistRepository,
        coroutineScope = screenModelScope
    )
}
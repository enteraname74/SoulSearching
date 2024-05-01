package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.SelectedMonthViewModel
import com.github.soulsearching.elementpage.monthpage.domain.SelectedMonthViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager

/**
 * Implementation of the SelectedMonthViewModel for Desktop
 */
class SelectedMonthViewModelDesktopImpl(
    playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    playbackManager: PlaybackManager
) : SelectedMonthViewModel {
    override val handler: SelectedMonthViewModelHandler = SelectedMonthViewModelHandler(
        coroutineScope = screenModelScope,
        playlistRepository = playlistRepository,
        musicRepository = musicRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        playbackManager = playbackManager
    )
}
package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.elementpage.playlistpage.domain.SelectedPlaylistViewModelHandler
import com.github.soulsearching.model.PlaybackManagerDesktopImpl

/**
 * Implementation of the SelectedPlaylistViewModel.
 */
class SelectedPlaylistViewModelDesktopImpl(
    playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    playbackManager: PlaybackManagerDesktopImpl
) : SelectedPlaylistViewModel {
    override val handler: SelectedPlaylistViewModelHandler = SelectedPlaylistViewModelHandler(
        coroutineScope = screenModelScope,
        playlistRepository = playlistRepository,
        musicRepository = musicRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        playbackManager = playbackManager
    )
}
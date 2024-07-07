package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistViewModelHandler
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager

/**
 * Implementation of the SelectedPlaylistViewModel.
 */
class SelectedPlaylistViewModelAndroidImpl(
    playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    playbackManager: PlaybackManager
) : SelectedPlaylistViewModel, ViewModel() {
    override val handler: SelectedPlaylistViewModelHandler = SelectedPlaylistViewModelHandler(
        coroutineScope = viewModelScope,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicRepository = musicRepository,
        playbackManager = playbackManager
    )
}
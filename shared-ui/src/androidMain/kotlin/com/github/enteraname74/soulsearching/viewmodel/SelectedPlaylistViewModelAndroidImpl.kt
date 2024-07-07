package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.elementpage.playlistpage.domain.SelectedPlaylistViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager

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
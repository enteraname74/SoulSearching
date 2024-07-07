package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedMonthViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthViewModelHandler
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager

/**
 * Implementation of the SelectedMonthViewModel for Android
 */
class SelectedMonthViewModelAndroidImpl(
    playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    playbackManager: PlaybackManager
): SelectedMonthViewModel, ViewModel() {
    override val handler: SelectedMonthViewModelHandler = SelectedMonthViewModelHandler(
        coroutineScope = viewModelScope,
        playlistRepository = playlistRepository,
        musicRepository = musicRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        playbackManager = playbackManager
    )
}
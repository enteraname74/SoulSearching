package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.repository.ImageCoverRepository
import com.github.enteraname74.repository.MusicPlaylistRepository
import com.github.enteraname74.repository.PlaylistRepository
import com.github.soulsearching.classes.PlaylistEventHandler
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * View model for the modify playlist screen.
 */
class ModifyPlaylistViewModel(
    playlistRepository : PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    imageCoverRepository: ImageCoverRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistState()
    )

    private val playlistEventHandler = PlaylistEventHandler(
        privateState = _state,
        publicState = state,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        imageCoverRepository = imageCoverRepository
    )

    /**
     * Manage playlist events.
     */
    fun onPlaylistEvent(event : PlaylistEvent) {
        playlistEventHandler.handleEvent(event)
    }
}
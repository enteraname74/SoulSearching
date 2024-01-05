package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.PlaylistEventHandler
import com.github.soulsearching.database.dao.ImageCoverDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * View model for the modify playlist screen.
 */
@HiltViewModel
class ModifyPlaylistViewModel @Inject constructor(
    private val playlistDao : PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val imageCoverDao: ImageCoverDao
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
        playlistDao = playlistDao,
        musicPlaylistDao = musicPlaylistDao,
        imageCoverDao = imageCoverDao
    )

    /**
     * Manage playlist events.
     */
    fun onPlaylistEvent(event : PlaylistEvent) {
        playlistEventHandler.handleEvent(event)
    }
}
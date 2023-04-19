package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.EventUtils
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AllPlaylistsViewModel @Inject constructor(
    private val playlistDao : PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao
): ViewModel() {
    private val _playlists = playlistDao.getAllPlaylistsWithMusics().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(PlaylistState())
    // On combine nos 2 flows en un seul.
    val state = combine(_state, _playlists) {state, playlists ->
        state.copy(
            playlists = playlists
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistState()
    )

    fun onPlaylistEvent(event: PlaylistEvent) {
        EventUtils.onPlaylistEvent(
            event = event,
            _state = _state,
            state = state,
            playlistDao = playlistDao,
            musicPlaylistDao = musicPlaylistDao
        )
    }
}
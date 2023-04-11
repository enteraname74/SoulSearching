package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.database.model.PlaylistWithMusics
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.states.SelectedPlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SelectedPlaylistViewModel @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val musicDao: MusicDao
) : ViewModel() {
    private lateinit var _selectedPlaylistMusics: StateFlow<PlaylistWithMusics>

    // On combine nos 2 flows en un seul.
    private val _state = MutableStateFlow(SelectedPlaylistState())
    lateinit var state: StateFlow<SelectedPlaylistState>

    fun getSelectedPlaylist(playlistId: UUID) {
        _selectedPlaylistMusics = playlistDao
            .getPlaylistWithSongs(playlistId = playlistId)
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(), PlaylistWithMusics(
                    Playlist(
                        UUID.randomUUID(),
                        ""
                    ),
                    emptyList()
                )
            )

        state = combine(_state, _selectedPlaylistMusics) { state, playlist ->
            state.copy(
                playlistWithMusics = playlist
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedPlaylistState()
        )
    }

    fun onPlaylistsEvent(event: PlaylistEvent) {
        when (event) {
        }
    }
}
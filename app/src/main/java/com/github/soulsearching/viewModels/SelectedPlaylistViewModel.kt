package com.github.soulsearching.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.database.model.PlaylistWithMusics
import com.github.soulsearching.events.MusicEvent
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
    var state: StateFlow<SelectedPlaylistState> = _state

    fun setSelectedPlaylist(playlistId: UUID) {
        Log.d("Id used :", playlistId.toString())
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
        Log.d("Playlist info : ", _selectedPlaylistMusics.value.toString())
        _state.update { it.copy(
            playlistWithMusics = _selectedPlaylistMusics.value
        ) }
    }

    fun onPlaylistsEvent(event: PlaylistEvent) {
        when (event) {
        }
    }

    fun onMusicEvent(event: MusicEvent) {
    }
}
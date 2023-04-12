package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AllPlaylistsViewModel @Inject constructor(
    private val dao : PlaylistDao
): ViewModel() {
    private val _playlists = dao.getAllPlaylists().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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

    fun onPlaylistsEvent(event: PlaylistEvent) {
        when(event){
            PlaylistEvent.AddPlaylist -> {
                viewModelScope.launch {
                    dao.insertPlaylist(
                        Playlist(
                            playlistId = UUID.randomUUID(),
                            name = "test"
                        )
                    )
                }
            }
            is PlaylistEvent.DeletePlaylist -> {
                viewModelScope.launch {
                    dao.deletePlaylist(event.playlist)
                }
            }
            is PlaylistEvent.SetSelectedPlaylist -> {
                _state.update { it.copy(
                    selectedPlaylist = event.playlist
                ) }
            }
        }
    }
}
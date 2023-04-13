package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyPlaylistViewModel @Inject constructor(
    private val playlistDao : PlaylistDao
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistState()
    )

    fun onPlaylistEvent(event : PlaylistEvent) {
        when(event) {
            PlaylistEvent.UpdatePlaylist -> {
                viewModelScope.launch {
                    playlistDao.insertPlaylist(
                        Playlist(
                            playlistId = state.value.selectedPlaylist!!.playlistId,
                            name = state.value.name,
                            playlistCover = state.value.cover
                        )
                    )
                }
            }
            is PlaylistEvent.PlaylistFromId -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlist = playlistDao.getPlaylistFromId(event.playlistId)
                    _state.update { it.copy(
                        selectedPlaylist = playlist,
                        name = playlist.name,
                        cover = playlist.playlistCover
                    ) }
                }
            }
            is PlaylistEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is PlaylistEvent.SetCover -> {
                _state.update { it.copy(
                    cover = event.cover
                ) }
            }
            else -> {}
        }
    }
}
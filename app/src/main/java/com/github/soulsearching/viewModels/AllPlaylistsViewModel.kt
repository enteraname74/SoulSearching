package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.MusicPlaylist
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class AllPlaylistsViewModel @Inject constructor(
    private val playlistDao : PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao
): ViewModel() {
    private val _playlists = playlistDao.getAllPlaylists().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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
            is PlaylistEvent.AddPlaylist -> {
                viewModelScope.launch {
                    playlistDao.insertPlaylist(
                        Playlist(
                            playlistId = UUID.randomUUID(),
                            name = "test"
                        )
                    )
                }
            }
            is PlaylistEvent.AddMusicToPlaylists -> {
                viewModelScope.launch {
                    for (selectedPlaylistId in state.value.multiplePlaylistSelected) {
                        musicPlaylistDao.insertMusicIntoPlaylist(
                            MusicPlaylist(
                                musicId = event.musicId,
                                playlistId = selectedPlaylistId
                            )
                        )
                    }
                    _state.update {it.copy(
                        multiplePlaylistSelected = ArrayList()
                    )
                    }
                }
            }
            is PlaylistEvent.DeletePlaylist -> {
                viewModelScope.launch {
                    playlistDao.deletePlaylist(event.playlist)
                }
            }
            is PlaylistEvent.SetSelectedPlaylist -> {
                _state.update { it.copy(
                    selectedPlaylist = event.playlist
                ) }
            }
            is PlaylistEvent.TogglePlaylistSelectedState -> {
                val newList = ArrayList(state.value.multiplePlaylistSelected)
                if (event.playlistId in newList) newList.remove(event.playlistId)
                else newList.add(event.playlistId)

                _state.update {it.copy(
                    multiplePlaylistSelected = newList
                )
                }
            }
            is PlaylistEvent.PlaylistsSelection -> {
                viewModelScope.launch {
                    val playlists = playlistDao.getAllPlaylistsWithMusicsSimple().filter { playlistWithMusics ->
                        playlistWithMusics.musics.find { it.musicId == event.musicId } == null
                    }
                    _state.update { it.copy(
                        multiplePlaylistSelected = ArrayList(),
                        playlistsWithoutMusicId = playlists
                    ) }
                }
            }
        }
    }
}
package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.MusicPlaylist
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.database.model.PlaylistWithMusics
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedPlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SelectedPlaylistViewModel @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val musicDao: MusicDao,
    private val musicPlaylistDao: MusicPlaylistDao
) : ViewModel() {
    private lateinit var _selectedPlaylistMusics: StateFlow<PlaylistWithMusics>

    // On combine nos 2 flows en un seul.
    private val _playlistState = MutableStateFlow(SelectedPlaylistState())
    var playlistState: StateFlow<SelectedPlaylistState> = _playlistState

    private val _musicState = MutableStateFlow(MusicState())
    var musicState : StateFlow<MusicState> = _musicState

    fun setSelectedPlaylist(playlistId: UUID) {
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

        playlistState = combine(_playlistState, _selectedPlaylistMusics) { state, playlist ->
            state.copy(
                playlistWithMusics = playlist
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedPlaylistState()
        )

        musicState = combine(_musicState, _selectedPlaylistMusics) { state, playlist ->
            state.copy(
                musics = playlist.musics
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicState()
        )

        _playlistState.update { it.copy(
            playlistWithMusics = _selectedPlaylistMusics.value
        ) }

        _musicState.update { it.copy(
            musics = _selectedPlaylistMusics.value.musics
        ) }
    }

    fun onPlaylistsEvent(event: PlaylistEvent) {
    }

    fun onMusicEvent(event: MusicEvent) {
        when(event) {
            is MusicEvent.DeleteDialog -> {
                _musicState.update { it.copy(
                    isDeleteDialogShown = event.isShown
                ) }
            }
            MusicEvent.DeleteMusic -> {
                viewModelScope.launch {
                    musicDao.deleteMusic(musicState.value.selectedMusic as Music)
                    musicPlaylistDao.deleteMusicFromAllPlaylists(musicState.value.selectedMusic!!.musicId)
                }
            }
            MusicEvent.AddMusic -> {
                val name = musicState.value.name

                val music = Music(
                    musicId = UUID.randomUUID(),
                    name = name,
                    album = "",
                    artist = "",
                    duration = 1000L,
                    path = ""
                )

                viewModelScope.launch {
                    musicDao.insertMusic(music)
                }
            }
            is MusicEvent.SetSelectedMusic -> {
                _musicState.update { it.copy(
                    selectedMusic = event.music
                ) }
            }
            MusicEvent.AddToPlaylist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val firstPlaylistId = playlistDao.getFirstPlaylistId()
                    musicPlaylistDao.insertMusicIntoPlaylist(
                        MusicPlaylist(
                            musicId = musicState.value.selectedMusic!!.musicId,
                            playlistId = firstPlaylistId
                        )
                    )
                }
            }
            is MusicEvent.BottomSheet -> {
                _musicState.update { it.copy(
                    isBottomSheetShown = event.isShown
                ) }
            }
            else -> {}
        }
    }
}
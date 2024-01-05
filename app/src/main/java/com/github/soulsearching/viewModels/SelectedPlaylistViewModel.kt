package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.utils.EventUtils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Music
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
import kotlin.collections.ArrayList

/**
 * View model for the selected playlist screen.
 */
@HiltViewModel
class SelectedPlaylistViewModel @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val musicDao: MusicDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val albumArtistDao: AlbumArtistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao,
    private val imageCoverDao: ImageCoverDao
) : ViewModel() {
    private var _selectedPlaylistMusics: StateFlow<PlaylistWithMusics?> = MutableStateFlow(
        PlaylistWithMusics()
    )

    private val _selectedPlaylistState = MutableStateFlow(SelectedPlaylistState())
    var selectedPlaylistState: StateFlow<SelectedPlaylistState> = _selectedPlaylistState

    private val _musicState = MutableStateFlow(MusicState())
    var musicState: StateFlow<MusicState> = _musicState

    /**
     * Set the selected playlist.
     */
    fun setSelectedPlaylist(playlistId: UUID) {
        _selectedPlaylistMusics = playlistDao
            .getPlaylistWithMusics(playlistId = playlistId)
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(), PlaylistWithMusics()
            )

        selectedPlaylistState = combine(_selectedPlaylistState,
            _selectedPlaylistMusics
        ) { state, playlist ->
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
                musics =  if (playlist?.musics?.isNotEmpty() == true) {
                    playlist.musics.filter { !it.isHidden } as ArrayList<Music>
                } else {
                    ArrayList()
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicState()
        )

        _selectedPlaylistState.update {
            it.copy(
                playlistWithMusics = _selectedPlaylistMusics.value
            )
        }

        _musicState.update {
            it.copy(
                musics = if (_selectedPlaylistMusics.value!!.musics.isNotEmpty()) {
                    _selectedPlaylistMusics.value!!.musics as ArrayList<Music>
                }  else {
                    ArrayList()
                }
            )
        }
    }

    /**
     * Manage playlist events.
     */
    fun onPlaylistEvent(event: PlaylistEvent) {
        when(event) {
            is PlaylistEvent.AddNbPlayed -> {
                CoroutineScope(Dispatchers.IO).launch {
                    playlistDao.updateNbPlayed(
                        newNbPlayed = playlistDao.getNbPlayedOfPlaylist(event.playlistId) + 1,
                        playlistId = event.playlistId
                    )
                }
            }
            else -> {}
        }
    }

    /**
     * Manage music events.
     */
    fun onMusicEvent(event : MusicEvent) {
        EventUtils.onMusicEvent(
            event = event,
            _state = _musicState,
            state = musicState,
            musicDao = musicDao,
            playlistDao = playlistDao,
            albumDao = albumDao,
            artistDao = artistDao,
            musicPlaylistDao = musicPlaylistDao,
            musicAlbumDao = musicAlbumDao,
            musicArtistDao = musicArtistDao,
            albumArtistDao = albumArtistDao,
            imageCoverDao = imageCoverDao
        )
    }
}
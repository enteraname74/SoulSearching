package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.MusicEventHandler
import com.github.soulsearching.database.dao.AlbumArtistDao
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.database.dao.ArtistDao
import com.github.soulsearching.database.dao.ImageCoverDao
import com.github.soulsearching.database.dao.MusicAlbumDao
import com.github.soulsearching.database.dao.MusicArtistDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.PlaylistWithMusics
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedPlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * View model for the selected playlist screen.
 */
@HiltViewModel
class SelectedPlaylistViewModel @Inject constructor(
    private val playlistDao: PlaylistDao,
    musicDao: MusicDao,
    artistDao: ArtistDao,
    albumDao: AlbumDao,
    albumArtistDao: AlbumArtistDao,
    musicPlaylistDao: MusicPlaylistDao,
    musicAlbumDao: MusicAlbumDao,
    musicArtistDao: MusicArtistDao,
    imageCoverDao: ImageCoverDao
) : ViewModel() {
    private var _selectedPlaylistMusics: StateFlow<PlaylistWithMusics?> = MutableStateFlow(
        PlaylistWithMusics()
    )

    private val _selectedPlaylistState = MutableStateFlow(SelectedPlaylistState())
    var selectedPlaylistState: StateFlow<SelectedPlaylistState> = _selectedPlaylistState

    private val _musicState = MutableStateFlow(MusicState())
    var musicState: StateFlow<MusicState> = _musicState

    private val musicEventHandler = MusicEventHandler(
        privateState = _musicState,
        publicState = musicState,
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
        musicEventHandler.handleEvent(event)
    }
}
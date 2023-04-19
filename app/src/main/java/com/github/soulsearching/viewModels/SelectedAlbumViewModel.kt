package com.github.soulsearching.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.AlbumWithMusics
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedAlbumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SelectedAlbumViewModel @Inject constructor(
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val musicDao: MusicDao,
    private val playlistDao: PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao,
    private val albumArtistDao: AlbumArtistDao
) : ViewModel() {
    private var _selectedAlbumWithMusics : StateFlow<AlbumWithMusics> = MutableStateFlow(AlbumWithMusics())

    private val _selectedAlbumState = MutableStateFlow(SelectedAlbumState())
    var selectedAlbumState: StateFlow<SelectedAlbumState> = _selectedAlbumState

    private val _musicState = MutableStateFlow(MusicState())
    var musicState: StateFlow<MusicState> = _musicState

    fun setSelectedAlbum(albumId: UUID) {
        _selectedAlbumWithMusics = albumDao
            .getAlbumWithMusics(albumId = albumId)
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(), AlbumWithMusics()
            )

        selectedAlbumState = combine(_selectedAlbumState, _selectedAlbumWithMusics) { state, album ->
            state.copy(
                albumWithMusics = album
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedAlbumState()
        )

        musicState = combine(_musicState, _selectedAlbumWithMusics) { state, album ->
            state.copy(
                musics = album.musics
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicState()
        )

        _selectedAlbumState.update {
            it.copy(
                albumWithMusics = _selectedAlbumWithMusics.value
            )
        }

        _musicState.update {
            it.copy(
                musics = _selectedAlbumWithMusics.value.musics
            )
        }
    }

    fun onMusicEvent(event: MusicEvent) {
        Utils.onMusicEvent(
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
            albumArtistDao = albumArtistDao
        )
    }

    fun onAlbumEvent(event : AlbumEvent) {
        Log.d("EVENT", event.toString())
        when(event) {
        }
    }
}
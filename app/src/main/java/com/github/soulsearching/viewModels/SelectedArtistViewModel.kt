package com.github.soulsearching.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.ArtistWithMusics
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedArtistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SelectedArtistViewModel @Inject constructor(
    private val artistDao: ArtistDao,
    private val musicDao: MusicDao,
    private val albumDao: AlbumDao,
    private val playlistDao: PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao
) : ViewModel() {
    private var _selectedArtistWithMusics : StateFlow<ArtistWithMusics> = MutableStateFlow(ArtistWithMusics())

    private val _selectedArtistState = MutableStateFlow(SelectedArtistState())
    var selectedArtistState: StateFlow<SelectedArtistState> = _selectedArtistState

    private val _musicState = MutableStateFlow(MusicState())
    var musicState: StateFlow<MusicState> = _musicState

    fun setSelectedArtist(albumId: UUID) {
        _selectedArtistWithMusics = artistDao
            .getArtistWithMusics(artistId = albumId)
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(), ArtistWithMusics()
            )

        selectedArtistState = combine(_selectedArtistState, _selectedArtistWithMusics) { state, artist ->
            state.copy(
                artistWithMusics = artist
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedArtistState()
        )

        musicState = combine(_musicState, _selectedArtistWithMusics) { state, album ->
            state.copy(
                musics = album.musics
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicState()
        )

        _selectedArtistState.update {
            it.copy(
                artistWithMusics = _selectedArtistWithMusics.value
            )
        }

        _musicState.update {
            it.copy(
                musics = _selectedArtistWithMusics.value.musics
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

    fun onArtistEvent(event : AlbumEvent) {
        Log.d("EVENT", event.toString())
        when(event) {
        }
    }
}
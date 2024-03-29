package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.classes.MusicEventHandler
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
    private val playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    artistRepository: ArtistRepository,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    imageCoverRepository: ImageCoverRepository
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
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        albumArtistRepository = albumArtistRepository,
        imageCoverRepository = imageCoverRepository
    )

    /**
     * Set the selected playlist.
     */
    fun setSelectedPlaylist(playlistId: UUID) {
        _selectedPlaylistMusics = playlistRepository
            .getPlaylistWithMusicsAsFlow(playlistId = playlistId)
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
                    playlistRepository.updateNbPlayed(
                        newNbPlayed = playlistRepository.getNbPlayedOfPlaylist(event.playlistId) + 1,
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
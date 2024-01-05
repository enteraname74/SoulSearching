package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.utils.EventUtils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.AlbumWithMusics
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedAlbumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * View model for the selected album screen.
 */
@HiltViewModel
class SelectedAlbumViewModel @Inject constructor(
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val musicDao: MusicDao,
    private val playlistDao: PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val imageCoverDao: ImageCoverDao
) : ViewModel() {
    private var _selectedAlbumWithMusics : StateFlow<AlbumWithMusics?> = MutableStateFlow(AlbumWithMusics())

    private val _selectedAlbumState = MutableStateFlow(SelectedAlbumState())
    var selectedAlbumState: StateFlow<SelectedAlbumState> = _selectedAlbumState

    private val _musicState = MutableStateFlow(MusicState())
    var musicState: StateFlow<MusicState> = _musicState

    /**
     * Set the selected album.
     */
    fun setSelectedAlbum(albumId: UUID) {
        _selectedAlbumWithMusics = albumDao
            .getAlbumWithMusics(albumId = albumId)
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(), AlbumWithMusics()
            )

        selectedAlbumState = combine(_selectedAlbumState, _selectedAlbumWithMusics) { state, album ->
            state.copy(
                albumWithMusics = album ?: AlbumWithMusics()
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedAlbumState()
        )

        musicState = combine(_musicState, _selectedAlbumWithMusics) { state, album ->
            state.copy(
                musics = if (album?.musics?.isNotEmpty() == true) {
                    album.musics.filter { !it.isHidden } as ArrayList<Music>
                } else {
                    ArrayList()
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicState()
        )
        _selectedAlbumState.update {
            it.copy(
                albumWithMusics = _selectedAlbumWithMusics.value ?: AlbumWithMusics()
            )
        }

        _musicState.update {
            it.copy(
                musics = if (_selectedAlbumWithMusics.value?.musics?.isNotEmpty() == true) {
                    _selectedAlbumWithMusics.value?.musics as ArrayList<Music>
                } else {
                    ArrayList()
                }
            )
        }
    }

    /**
     * Check if an album exists.
     */
    fun doesAlbumExists(albumId : UUID) : Boolean{
        return albumDao.getAlbumFromId(
            albumId
        ) == null
    }

    /**
     * Manage album events.
     */
    fun onAlbumEvent(event: AlbumEvent) {
        when(event) {
            is AlbumEvent.AddNbPlayed -> {
                CoroutineScope(Dispatchers.IO).launch {
                    albumDao.updateNbPlayed(
                        newNbPlayed = albumDao.getNbPlayedOfAlbum(event.albumId) + 1,
                        albumId = event.albumId
                    )
                }
            }
            else -> {}
        }
    }

    /**
     * Manage music event.
     */
    fun onMusicEvent(event: MusicEvent) {
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
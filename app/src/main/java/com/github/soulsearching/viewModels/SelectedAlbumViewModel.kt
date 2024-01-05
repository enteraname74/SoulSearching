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
import com.github.soulsearching.database.model.AlbumWithMusics
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedAlbumState
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
 * View model for the selected album screen.
 */
@HiltViewModel
class SelectedAlbumViewModel @Inject constructor(
    private val albumDao: AlbumDao,
    artistDao: ArtistDao,
    musicDao: MusicDao,
    playlistDao: PlaylistDao,
    musicPlaylistDao: MusicPlaylistDao,
    musicAlbumDao: MusicAlbumDao,
    musicArtistDao: MusicArtistDao,
    albumArtistDao: AlbumArtistDao,
    imageCoverDao: ImageCoverDao
) : ViewModel() {
    private var _selectedAlbumWithMusics : StateFlow<AlbumWithMusics?> = MutableStateFlow(AlbumWithMusics())

    private val _selectedAlbumState = MutableStateFlow(SelectedAlbumState())
    var selectedAlbumState: StateFlow<SelectedAlbumState> = _selectedAlbumState

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
        musicEventHandler.handleEvent(event)
    }
}
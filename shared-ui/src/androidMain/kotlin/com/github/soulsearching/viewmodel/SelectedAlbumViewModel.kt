package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Music
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
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedAlbumState
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

/**
 * View model for the selected album screen.
 */
class SelectedAlbumViewModel(
    private val albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings
) : ViewModel() {
    private var _selectedAlbumWithMusics : StateFlow<AlbumWithMusics?> = MutableStateFlow(
        AlbumWithMusics()
    )

    private val _selectedAlbumState = MutableStateFlow(SelectedAlbumState())
    var selectedAlbumState: StateFlow<SelectedAlbumState> = _selectedAlbumState

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
        imageCoverRepository = imageCoverRepository,
        settings = settings
    )

    /**
     * Set the selected album.
     */
    fun setSelectedAlbum(albumId: UUID) {
        _selectedAlbumWithMusics = albumRepository
            .getAlbumWithMusicsAsFlow(albumId = albumId)
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
    suspend fun doesAlbumExists(albumId : UUID) : Boolean{
        return albumRepository.getAlbumFromId(
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
                    albumRepository.updateNbPlayed(
                        newNbPlayed = albumRepository.getNbPlayedOfAlbum(event.albumId) + 1,
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
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
import com.github.soulsearching.database.model.ArtistWithMusics
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedArtistState
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
 * View model for the selected artist screen.
 */
@HiltViewModel
class SelectedArtistViewModel @Inject constructor(
    private val artistDao: ArtistDao,
    musicDao: MusicDao,
    albumDao: AlbumDao,
    playlistDao: PlaylistDao,
    musicPlaylistDao: MusicPlaylistDao,
    albumArtistDao: AlbumArtistDao,
    musicAlbumDao: MusicAlbumDao,
    musicArtistDao: MusicArtistDao,
    imageCoverDao: ImageCoverDao
) : ViewModel() {
    private var _selectedArtistWithMusics : StateFlow<ArtistWithMusics?> = MutableStateFlow(ArtistWithMusics())

    private val _selectedArtistState = MutableStateFlow(SelectedArtistState())
    var selectedArtistState: StateFlow<SelectedArtistState> = _selectedArtistState

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
     * Set the selected artist.
     */
    fun setSelectedArtist(artistId: UUID) {
        _selectedArtistWithMusics = artistDao
            .getArtistWithMusics(artistId = artistId)
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(), ArtistWithMusics()
            )

        selectedArtistState = combine(_selectedArtistState, _selectedArtistWithMusics) { state, artist ->
            state.copy(
                artistWithMusics = artist ?: ArtistWithMusics()
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedArtistState()
        )

        musicState = combine(_musicState, _selectedArtistWithMusics) { state, artist ->
            state.copy(
                musics = if (artist?.musics?.isNotEmpty() == true) {
                    artist.musics.filter { !it.isHidden } as ArrayList<Music>
                }  else {
                    ArrayList()
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicState()
        )

        _selectedArtistState.update {
            it.copy(
                artistWithMusics = _selectedArtistWithMusics.value ?: ArtistWithMusics()
            )
        }

        _musicState.update {
            it.copy(
                musics = if (_selectedArtistWithMusics.value?.musics?.isNotEmpty() == true) {
                    _selectedArtistWithMusics.value?.musics as ArrayList<Music>
                }  else {
                    ArrayList()
                }
            )
        }
    }

    /**
     * Check if an artist exists.
     */
    fun doesArtistExists(artistId : UUID) : Boolean{
        return artistDao.getArtistFromId(
            artistId
        ) == null
    }

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }

    /**
     * Manage artist events.
     */
    fun onArtistEvent(event : ArtistEvent) {
        when(event) {
            is ArtistEvent.AddNbPlayed -> {
                CoroutineScope(Dispatchers.IO).launch {
                    artistDao.updateNbPlayed(
                        newNbPlayed = artistDao.getNbPlayedOfArtist(event.artistId) + 1,
                        artistId = event.artistId
                    )
                }
            }
            else -> {}
        }
    }
}
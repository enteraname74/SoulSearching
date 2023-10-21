package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.EventUtils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.ArtistWithMusics
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedArtistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    private val musicArtistDao: MusicArtistDao,
    private val imageCoverDao: ImageCoverDao
) : ViewModel() {
    private var _selectedArtistWithMusics : StateFlow<ArtistWithMusics?> = MutableStateFlow(ArtistWithMusics())

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

    fun checkIfArtistIdDeleted(artistId : UUID) : Boolean{
        return artistDao.getArtistFromId(
            artistId
        ) == null
    }

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
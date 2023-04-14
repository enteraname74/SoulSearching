package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.ArtistDao
import com.github.soulsearching.database.dao.MusicArtistDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.SelectedArtistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyArtistViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val artistDao: ArtistDao,
    private val musicArtistDao: MusicArtistDao
) : ViewModel() {

    private val _state = MutableStateFlow(SelectedArtistState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedArtistState()
    )

    fun onArtistEvent(event: ArtistEvent) {
        when (event) {
            ArtistEvent.UpdateArtist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    artistDao.insertArtist(
                        Artist(
                            artistId = state.value.artistWithMusics.artist.artistId,
                            name = state.value.artistWithMusics.artist.name,
                            artistCover = state.value.artistWithMusics.artist.artistCover,
                        )
                    )
                }
                CoroutineScope(Dispatchers.IO).launch {
                    // On met à jour les musiques de l'album :
                    for (music in state.value.artistWithMusics.musics) {
                        musicDao.insertMusic(
                            music.copy(
                                artist = state.value.artistWithMusics.artist.name
                            )
                        )
                    }
                }
            }
            is ArtistEvent.ArtistFromId -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val artist = artistDao.getArtistWithMusicsSimple(event.artistId)
                    _state.update {
                        it.copy(
                            artistWithMusics = artist
                        )
                    }
                }
            }
            is ArtistEvent.SetName -> {
                _state.update {
                    it.copy(
                        artistWithMusics = it.artistWithMusics.copy(
                            artist = it.artistWithMusics.artist.copy(
                                name = event.name
                            )
                        )
                    )
                }
            }
            is ArtistEvent.SetCover -> {
                _state.update {
                    it.copy(
                        artistWithMusics = it.artistWithMusics.copy(
                            artist = it.artistWithMusics.artist.copy(
                                artistCover = event.cover
                            )
                        )
                    )
                }
            }
            else -> {}
        }
    }
}
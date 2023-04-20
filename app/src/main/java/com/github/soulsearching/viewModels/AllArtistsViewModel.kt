package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.database.dao.ArtistDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.ArtistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllArtistsViewModel @Inject constructor(
    private val artistDao: ArtistDao,
    private val musicDao: MusicDao,
    private val albumDao: AlbumDao
) : ViewModel() {
    private val _artists = artistDao.getAllArtistsWithMusics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(ArtistState())
    val state = combine(_artists, _state) { artists, state ->
        state.copy(
            artists = artists
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ArtistState()
    )

    fun onArtistEvent(event: ArtistEvent) {
        when (event) {
            ArtistEvent.DeleteArtist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    // On supprime d'abord les musiques de l'artiste :
                    for (music in state.value.selectedArtistWithMusics.musics){
                        musicDao.deleteMusic(music)
                    }
                    // On supprime ensuite tous les albums de l'artiste :
                    val albumsToDelete = albumDao.getAllAlbumsFromArtist(
                        artistId = state.value.selectedArtistWithMusics.artist.artistId
                    )
                    for (album in albumsToDelete) {
                        albumDao.deleteAlbum(album)
                    }
                    // On supprime enfin l'artiste :
                    artistDao.deleteArtist(state.value.selectedArtistWithMusics.artist)
                }
            }
            is ArtistEvent.SetSelectedArtist -> {
                _state.update {
                    it.copy(
                        selectedArtistWithMusics = event.artistWithMusics
                    )
                }
            }
            is ArtistEvent.BottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetShown = event.isShown
                    )
                }
            }
            is ArtistEvent.DeleteDialog -> {
                _state.update {
                    it.copy(
                        isDeleteDialogShown = event.isShown
                    )
                }
            }
            else -> {}
        }
    }
}
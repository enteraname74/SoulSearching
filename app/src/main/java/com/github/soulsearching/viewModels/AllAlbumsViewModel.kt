package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.database.dao.ArtistDao
import com.github.soulsearching.database.dao.MusicArtistDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllAlbumsViewModel @Inject constructor(
    private val albumDao : AlbumDao,
    private val musicDao: MusicDao,
    private val artistDao: ArtistDao,
    private val musicArtistDao: MusicArtistDao
) : ViewModel() {
    private val _albums = albumDao.getAllAlbumsWithArtist().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(AlbumState())
    val state = combine(_albums, _state) { albums, state ->
        state.copy(
            albums = albums
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AlbumState()
    )

    fun onAlbumEvent(event : AlbumEvent) {
        when(event){
            AlbumEvent.DeleteAlbum -> {
                CoroutineScope(Dispatchers.IO).launch {
                    // On supprime d'abord les musiques de l'album :
                    musicDao.deleteMusicFromAlbum(
                        album = state.value.selectedAlbumWithArtist.album.albumName,
                        artist = state.value.selectedAlbumWithArtist.artist!!.artistName
                    )
                    // On supprime ensuite l'album :
                    albumDao.deleteAlbum(state.value.selectedAlbumWithArtist.album)

                    // On vérifie si on peut supprimer l'artiste :
                    Utils.checkAndDeleteArtist(
                        artistToCheck = state.value.selectedAlbumWithArtist.artist!!,
                        musicArtistDao = musicArtistDao,
                        artistDao = artistDao
                    )
                }
            }

            is AlbumEvent.DeleteAlbum ->
                viewModelScope.launch {
                }
            is AlbumEvent.SetSelectedAlbum -> {
                _state.update { it.copy(
                    selectedAlbumWithArtist = event.albumWithArtist
                ) }
            }
            is AlbumEvent.BottomSheet -> {
                _state.update { it.copy(
                    isBottomSheetShown = event.isShown
                ) }
            }
            is AlbumEvent.DeleteDialog -> {
                _state.update { it.copy(
                    isDeleteDialogShown = event.isShown
                ) }
            }
            else -> {}
        }
    }
}
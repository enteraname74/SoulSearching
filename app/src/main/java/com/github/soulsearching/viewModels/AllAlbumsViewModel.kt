package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllAlbumsViewModel @Inject constructor(
    private val albumDao : AlbumDao
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
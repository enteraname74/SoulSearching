package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState
import com.github.soulsearching.states.AlbumWithArtistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AllAlbumsViewModel @Inject constructor(
    private val albumDao : AlbumDao
) : ViewModel() {
    private val _albums = albumDao.getAllAlbumsWithArtist().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(AlbumWithArtistState())
    val state = combine(_albums, _state) { albums, state ->
        state.copy(
            albums = albums
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AlbumWithArtistState()
    )

    fun onAlbumEvent(event : AlbumEvent) {
        when(event){
            else -> {}
        }
    }
}
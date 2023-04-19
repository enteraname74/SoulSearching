package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.ArtistDao
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.ArtistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AllArtistsViewModel @Inject constructor(
    private val artistDao : ArtistDao
) : ViewModel() {
    private val _artists = artistDao.getAllArtistsWithMusics().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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

    fun onArtistEvent(event : ArtistEvent) {
        when(event){
            else -> {}
        }
    }
}
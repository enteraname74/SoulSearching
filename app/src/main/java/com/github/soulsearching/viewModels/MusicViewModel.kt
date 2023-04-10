package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class MusicViewModel(
    private val dao : MusicDao
): ViewModel() {
    private val _musics = dao.getAllMusics().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(MusicState())
    // On combine nos 2 flows en un seul.
    val state = combine(_state, _musics) {state, musics ->
        state.copy(
            musics = musics
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MusicState()
    )

    fun onEvent(event: MusicEvent) {
        when(event) {
            is MusicEvent.DeleteMusic -> {
                viewModelScope.launch {
                    dao.deleteMusic(event.music)
                }
            }
            MusicEvent.SaveMusic -> {
                val name = state.value.name

                val music = Music(
                    musicId = UUID.randomUUID(),
                    name = name,
                    album = "",
                    artist = "",
                    duration = 1000L,
                    path = ""
                )

                viewModelScope.launch {
                    dao.insertMusic(music)
                }
            }
            is MusicEvent.SetCover -> {
                _state.update { it.copy(
                    cover = event.cover
                ) }
            }
            is MusicEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
        }
    }
}
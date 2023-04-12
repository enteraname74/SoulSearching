package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicDao : MusicDao,
    private val musicPlaylistDao : MusicPlaylistDao
): ViewModel() {
    private val _musics = musicDao.getAllMusics().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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

    fun onMusicEvent(event: MusicEvent) {
        when(event) {
            is MusicEvent.DeleteMusic -> {
                viewModelScope.launch {
                    musicDao.deleteMusic(event.music)
                    musicPlaylistDao.deleteMusicFromAllPlaylists(event.music.musicId)
                }
            }
            MusicEvent.AddMusic -> {
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
                    musicDao.insertMusic(music)
                }
            }
            else -> {}
        }
    }
}
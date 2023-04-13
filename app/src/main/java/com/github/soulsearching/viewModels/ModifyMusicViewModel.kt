package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ModifyMusicViewModel @Inject constructor(
    private val musicDao : MusicDao
): ViewModel() {

    private val _state = MutableStateFlow(MusicState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MusicState()
    )

    fun getMusicFromId(musicId : UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val music = musicDao.getMusicFromId(musicId)
            _state.update {
                it.copy(
                    selectedMusic = music,
                    name = music.name,
                    cover = music.albumCover,
                    album = music.album,
                    artist = music.artist
                )
            }
        }
    }

    fun onMusicEvent(event: MusicEvent) {
        when(event) {
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
            is MusicEvent.SetArtist -> {
                _state.update { it.copy(
                    artist = event.artist
                ) }
            }
            is MusicEvent.SetAlbum -> {
                _state.update { it.copy(
                    album = event.album
                ) }
            }
            is MusicEvent.UpdateMusic -> {
                viewModelScope.launch {
                    musicDao.insertMusic(
                        Music(
                            musicId = state.value.selectedMusic!!.musicId,
                            name = state.value.name,
                            album = state.value.album,
                            artist = state.value.artist,
                            albumCover = state.value.cover,
                            path = state.value.selectedMusic!!.path,
                            duration = state.value.selectedMusic!!.duration
                        )
                    )
                }
            }
            else -> {}
        }
    }
}
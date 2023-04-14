package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.SelectedAlbumState
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
class ModifyAlbumViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val albumDao: AlbumDao
) : ViewModel() {
    private val _state = MutableStateFlow(SelectedAlbumState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedAlbumState()
    )

    fun onAlbumEvent(event: AlbumEvent) {
        when (event) {
            AlbumEvent.UpdateAlbum -> {
                CoroutineScope(Dispatchers.IO).launch {
                    albumDao.insertAlbum(
                        Album(
                            albumId = state.value.albumWithMusics.album.albumId,
                            albumName = state.value.albumWithMusics.album.albumName,
                            albumCover = state.value.albumWithMusics.album.albumCover,
                            artist = state.value.albumWithMusics.album.artist,
                        )
                    )
                }
                CoroutineScope(Dispatchers.IO).launch {
                    // On met à jour les musiques de l'album :
                    for (music in state.value.albumWithMusics.musics) {
                        musicDao.insertMusic(
                            music.copy(
                                album = state.value.albumWithMusics.album.albumName,
                                albumCover = state.value.albumWithMusics.album.albumCover,
                                artist = state.value.albumWithMusics.album.artist
                            )
                        )
                    }
                }
            }
            is AlbumEvent.AlbumFromID -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val album = albumDao.getAlbumWithMusicsSimple(event.albumId)
                    _state.update {
                        it.copy(
                            albumWithMusics = album
                        )
                    }
                }
            }
            is AlbumEvent.SetName -> {
                _state.update {
                    it.copy(
                        albumWithMusics = it.albumWithMusics.copy(
                            album = it.albumWithMusics.album.copy(
                                albumName = event.name
                            )
                        )
                    )
                }
            }
            is AlbumEvent.SetCover -> {
                _state.update {
                    it.copy(
                        albumWithMusics = it.albumWithMusics.copy(
                            album = it.albumWithMusics.album.copy(
                                albumCover = event.cover
                            )
                        )
                    )
                }
            }
            is AlbumEvent.SetArtist -> {
                _state.update {
                    it.copy(
                        albumWithMusics = it.albumWithMusics.copy(
                            album = it.albumWithMusics.album.copy(
                                artist = event.artist
                            )
                        )
                    )
                }
            }
            else -> {}
        }
    }
}
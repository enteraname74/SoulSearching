package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.database.dao.MusicAlbumDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState
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
    private val albumDao: AlbumDao,
    private val musicAlbumDao: MusicAlbumDao
) : ViewModel() {
    private val _state = MutableStateFlow(AlbumState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AlbumState()
    )

    fun onAlbumEvent(event : AlbumEvent) {
        when(event) {
            AlbumEvent.UpdateAlbum -> {
                CoroutineScope(Dispatchers.IO).launch {
                    albumDao.insertAlbum(
                        Album(
                            albumId = state.value.selectedAlbum.albumId,
                            name = state.value.name,
                            albumCover = state.value.cover,
                            artist = state.value.artist,
                        )
                    )
                    // On met à jour les musiques de l'album :
                    val musicsIds = musicAlbumDao.getMusicsIdsFromAlbumId(state.value.selectedAlbum.albumId)
                    for (musicId in musicsIds) {
                        musicDao.insertMusic(
                            musicDao.getMusicFromId(musicId).copy(
                                album = state.value.name,
                                albumCover = state.value.cover,
                                artist = state.value.artist
                            )
                        )
                    }
                }
            }
            is AlbumEvent.AlbumFromID -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val album = albumDao.getAlbumFromId(event.albumId)
                    _state.update { it.copy(
                        selectedAlbum = album,
                        name = album.name,
                        artist = album.artist,
                        cover = album.albumCover
                    ) }
                }
            }
            is AlbumEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is AlbumEvent.SetCover -> {
                _state.update { it.copy(
                    cover = event.cover
                ) }
            }
            is AlbumEvent.SetArtist -> {
                _state.update { it.copy(
                    artist = event.artist
                ) }
            }
            else -> {}
        }
    }
}
package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithMusics
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedAlbumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SelectedAlbumViewModel @Inject constructor(
    private val albumDao: AlbumDao,
    private val musicDao: MusicDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicPlaylistDao : MusicPlaylistDao,
    private val musicArtistDao: MusicArtistDao
) : ViewModel() {
    private var _selectedAlbumWithMusics : StateFlow<AlbumWithMusics> = MutableStateFlow(AlbumWithMusics())

    private val _albumState = MutableStateFlow(SelectedAlbumState())
    var albumState: StateFlow<SelectedAlbumState> = _albumState

    private val _musicState = MutableStateFlow(MusicState())
    var musicState: StateFlow<MusicState> = _musicState

    fun setSelectedAlbum(albumId: UUID) {
        _selectedAlbumWithMusics = albumDao
            .getAlbumWithSongs(albumId = albumId)
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(), AlbumWithMusics()
            )

        albumState = combine(_albumState, _selectedAlbumWithMusics) { state, album ->
            state.copy(
                albumWithMusics = album
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedAlbumState()
        )

        musicState = combine(_musicState, _selectedAlbumWithMusics) { state, album ->
            state.copy(
                musics = album.musics
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicState()
        )

        _albumState.update {
            it.copy(
                albumWithMusics = _selectedAlbumWithMusics.value
            )
        }

        _musicState.update {
            it.copy(
                musics = _selectedAlbumWithMusics.value.musics
            )
        }
    }

    fun onMusicEvent(event: MusicEvent) {
        when (event) {
            is MusicEvent.DeleteDialog -> {
                _musicState.update {
                    it.copy(
                        isDeleteDialogShown = event.isShown
                    )
                }
            }
            MusicEvent.DeleteMusic -> {
                viewModelScope.launch {
                    Utils.removeMusicFromApp(
                        musicDao,
                        musicPlaylistDao,
                        musicAlbumDao,
                        musicArtistDao,
                        musicState.value.selectedMusic
                    )
                }
            }
            MusicEvent.AddMusic -> {
                val name = musicState.value.name

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
            is MusicEvent.SetSelectedMusic -> {
                _musicState.update {
                    it.copy(
                        selectedMusic = event.music
                    )
                }
            }
            MusicEvent.AddToPlaylist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    /*
                    val firstPlaylistId = playlistDao.getFirstPlaylistId()
                    musicPlaylistDao.insertMusicIntoPlaylist(
                        MusicPlaylist(
                            musicId = musicState.value.selectedMusic.musicId,
                            playlistId = firstPlaylistId
                        )
                    )
                     */
                }
            }
            is MusicEvent.BottomSheet -> {
                _musicState.update {
                    it.copy(
                        isBottomSheetShown = event.isShown
                    )
                }
            }
            else -> {}
        }
    }

    fun onAlbumEvent(event : AlbumEvent) {
        when(event) {
            AlbumEvent.UpdateAlbum -> {
                viewModelScope.launch {
                    albumDao.insertAlbum(
                        Album(
                            albumId = albumState.value.albumWithMusics.album.albumId,
                            name = albumState.value.albumWithMusics.album.name,
                            albumCover = albumState.value.albumWithMusics.album.albumCover
                        )
                    )
                }
            }
            is AlbumEvent.AlbumFromID -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val album = albumDao.getAlbumFromId(event.albumId)
                    _albumState.update { it.copy(
                        albumWithMusics = album,
                    ) }
                }
            }
            is AlbumEvent.SetName -> {
                _albumState.update { it.copy(
                    albumWithMusics = albumState.value.albumWithMusics.copy(
                        album = albumState.value.albumWithMusics.album.copy(
                            name = event.name
                        )
                    )
                ) }
            }
            is AlbumEvent.SetCover -> {
                _albumState.update { it.copy(
                    albumWithMusics = albumState.value.albumWithMusics.copy(
                        album = albumState.value.albumWithMusics.album.copy(
                            albumCover = event.cover
                        )
                    )
                ) }
            }
            else -> {}
        }
    }
}
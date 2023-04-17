package com.github.soulsearching.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.*
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AllMusicsViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val playlistDao: PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao,
    private val albumArtistDao: AlbumArtistDao
) : ViewModel() {
    private val _musics = musicDao.getAllMusics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(MusicState())

    // On combine nos 2 flows en un seul.
    val state = combine(_state, _musics) { state, musics ->
        state.copy(
            musics = musics
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MusicState()
    )

    fun onMusicEvent(event: MusicEvent) {
        when (event) {
            is MusicEvent.DeleteDialog -> {
                _state.update {
                    it.copy(
                        isDeleteDialogShown = event.isShown
                    )
                }
            }
            MusicEvent.DeleteMusic -> {
                CoroutineScope(Dispatchers.IO).launch {
                    Utils.removeMusicFromApp(
                        musicDao = musicDao,
                        albumDao = albumDao,
                        artistDao = artistDao,
                        albumArtistDao = albumArtistDao,
                        musicAlbumDao = musicAlbumDao,
                        musicArtistDao = musicArtistDao,
                        musicToRemove = state.value.selectedMusic
                    )
                }
            }
            MusicEvent.AddMusic -> {
                val music = Music(
                    musicId = UUID.randomUUID(),
                    name = "Nom Musique",
                    album = "Nom Album",
                    artist = "Nom Artiste",
                    duration = 1000L,
                    path = ""
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val correspondingArtist = artistDao.getArtistFromInfo(
                        artistName = music.artist
                    )
                    val allAlbums = albumDao.getAllAlbumsWithArtistSimple()
                    Log.d("Infos", allAlbums.toString())
                    val correspondingAlbum = allAlbums.find {
                        (it.album.albumName == music.album)
                                && (it.artist!!.artistName == music.artist)
                    }
                    var albumId = UUID.randomUUID()
                    var artistId = correspondingArtist?.artistId ?: UUID.randomUUID()
                    if (correspondingAlbum == null) {
                        albumDao.insertAlbum(
                            Album(
                                albumId = albumId,
                                albumName = music.album,
                                albumCover = music.albumCover,
                            )
                        )
                        artistDao.insertArtist(
                            Artist(
                                artistId = artistId,
                                artistName = music.artist,
                                artistCover = music.albumCover
                            )
                        )
                        albumArtistDao.insertAlbumIntoArtist(
                            AlbumArtist(
                                albumId = albumId,
                                artistId = artistId
                            )
                        )

                    } else {
                        albumId = correspondingAlbum.album.albumId
                        artistId = correspondingAlbum.artist!!.artistId
                        // Si la musique n'a pas de couverture, on lui donne celle de son album :
                        if (music.albumCover == null) {
                            music.albumCover = correspondingAlbum.album.albumCover
                        }
                    }
                    musicDao.insertMusic(music)
                    musicAlbumDao.insertMusicIntoAlbum(
                        MusicAlbum(
                            musicId = music.musicId,
                            albumId = albumId
                        )
                    )
                    musicArtistDao.insertMusicIntoArtist(
                        MusicArtist(
                            musicId = music.musicId,
                            artistId = artistId
                        )
                    )
                }
            }
            is MusicEvent.SetSelectedMusic -> {
                _state.update {
                    it.copy(
                        selectedMusic = event.music
                    )
                }
            }
            MusicEvent.AddToPlaylist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val firstPlaylistId = playlistDao.getFirstPlaylistId()
                    musicPlaylistDao.insertMusicIntoPlaylist(
                        MusicPlaylist(
                            musicId = state.value.selectedMusic.musicId,
                            playlistId = firstPlaylistId
                        )
                    )
                }
            }
            is MusicEvent.BottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetShown = event.isShown
                    )
                }
            }
            is MusicEvent.AddToPlaylistBottomSheet -> {
                _state.update {
                    it.copy(
                        isAddToPlaylistDialogShown = event.isShown
                    )
                }
            }
            else -> {}
        }
    }
}
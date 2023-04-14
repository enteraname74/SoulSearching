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
                viewModelScope.launch {
                    Utils.removeMusicFromApp(
                        musicDao,
                        musicPlaylistDao,
                        musicAlbumDao,
                        musicArtistDao,
                        state.value.selectedMusic
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
                    musicDao.insertMusic(music)

                    var correspondingAlbum = albumDao.getAlbumFromInfo(
                        name = music.album,
                        artist = music.artist
                    )

                    if (correspondingAlbum == null) {
                        val albumId = UUID.randomUUID()
                        val artistId = UUID.randomUUID()
                        albumDao.insertAlbum(
                            Album(
                                albumId = albumId,
                                albumName = music.album,
                                albumCover = music.albumCover,
                            )
                        )
                        val artist = Artist(
                            artistId = artistId,
                            artistName = music.artist,
                            artistCover = music.albumCover
                        )
                        Log.d("INFOS", artist.toString())
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
                        correspondingAlbum = albumDao.getAlbumFromInfo(
                            name = music.album,
                            artist = music.artist
                        )

                    }

                    musicAlbumDao.insertMusicIntoAlbum(
                        MusicAlbum(
                            musicId = music.musicId,
                            albumId = correspondingAlbum!!.album.albumId
                        )
                    )
                    musicArtistDao.insertMusicIntoArtist(
                        MusicArtist(
                            musicId = music.musicId,
                            artistId = correspondingAlbum.artist!!.artistId
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
                            musicId = state.value.selectedMusic!!.musicId,
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
            else -> {}
        }
    }
}
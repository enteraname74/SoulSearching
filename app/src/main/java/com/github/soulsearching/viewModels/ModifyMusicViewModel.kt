package com.github.soulsearching.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithArtist
import com.github.soulsearching.database.model.Artist
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
    private val musicDao: MusicDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val albumArtistDao: AlbumArtistDao,
    private val musicArtistDao: MusicArtistDao
) : ViewModel() {

    private val _state = MutableStateFlow(MusicState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MusicState()
    )

    fun getMusicFromId(musicId: UUID) {
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
        when (event) {
            is MusicEvent.SetCover -> {
                _state.update {
                    it.copy(
                        cover = event.cover
                    )
                }
            }
            is MusicEvent.SetName -> {
                _state.update {
                    it.copy(
                        name = event.name
                    )
                }
            }
            is MusicEvent.SetArtist -> {
                _state.update {
                    it.copy(
                        artist = event.artist
                    )
                }
            }
            is MusicEvent.SetAlbum -> {
                _state.update {
                    it.copy(
                        album = event.album
                    )
                }
            }
            is MusicEvent.UpdateMusic -> {
                CoroutineScope(Dispatchers.IO).launch {
                    if (state.value.selectedMusic.artist != state.value.artist.trim()) {
                        // On supprime d'abord l'ancien lien.
                        musicArtistDao.deleteMusicFromArtist(
                            musicId = state.value.selectedMusic.musicId
                        )

                        var newArtist = artistDao.getArtistFromInfo(
                            artistName = state.value.artist.trim()
                        )
                        if (newArtist == null) {
                            // C'est un nouvel artist, il faut le créer :
                            newArtist = Artist(
                                artistName = state.value.artist.trim(),
                                artistCover = state.value.cover
                            )
                            artistDao.insertArtist(
                                newArtist
                            )
                        }

                        // On vérifie si on peut supprimer l'ancien artiste :
                        val artistWithMusics = artistDao.getArtistWithMusicsSimple(
                            artistDao.getArtistFromInfo(
                                artistName = state.value.selectedMusic.artist
                            )!!.artistId
                        )
                        Utils.checkAndDeleteArtist(
                            artistToCheck = artistWithMusics.artist,
                            musicsFromArtist = artistWithMusics.musics,
                            artistDao = artistDao
                        )

                        // On met les infos de la musique à jour :
                        musicArtistDao.updateArtistOfMusic(
                            musicId = state.value.selectedMusic.musicId,
                            newArtistId = newArtist.artistId
                        )
                        Utils.modifyMusicAlbum(
                            musicAlbumDao = musicAlbumDao,
                            albumDao = albumDao,
                            artistDao = artistDao,
                            albumArtistDao = albumArtistDao,
                            legacyMusic = state.value.selectedMusic,
                            currentAlbum = state.value.album,
                            currentCover = state.value.cover,
                            currentArtist = state.value.artist
                        )
                    } else if (state.value.selectedMusic.album != state.value.album) {
                        Utils.modifyMusicAlbum(
                            musicAlbumDao = musicAlbumDao,
                            albumDao = albumDao,
                            artistDao = artistDao,
                            albumArtistDao = albumArtistDao,
                            legacyMusic = state.value.selectedMusic,
                            currentAlbum = state.value.album,
                            currentCover = state.value.cover,
                            currentArtist = state.value.artist
                        )
                    }

                    musicDao.insertMusic(
                        Music(
                            musicId = state.value.selectedMusic.musicId,
                            name = state.value.name.trim(),
                            album = state.value.album.trim(),
                            artist = state.value.artist.trim(),
                            albumCover = state.value.cover,
                            path = state.value.selectedMusic.path,
                            duration = state.value.selectedMusic.duration
                        )
                    )
                }
            }
            else -> {}
        }
    }
}
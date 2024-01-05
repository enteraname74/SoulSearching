package com.github.soulsearching.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.ArtistWithMusics
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.SelectedArtistState
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

/**
 * View model for the modify artist screen.
 */
@HiltViewModel
class ModifyArtistViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val artistDao: ArtistDao,
    private val musicArtistDao: MusicArtistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val albumArtistDao: AlbumArtistDao,
    private val albumDao: AlbumDao,
    private val imageCoverDao: ImageCoverDao
) : ViewModel() {

    private val _state = MutableStateFlow(SelectedArtistState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedArtistState()
    )

    /**
     * Manage artist events.
     */
    fun onArtistEvent(event: ArtistEvent) {
        when (event) {
            ArtistEvent.UpdateArtist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val coverId = if (state.value.hasCoverBeenChanged) {
                        val id = UUID.randomUUID()
                        imageCoverDao.insertImageCover(
                            ImageCover(
                                coverId = id,
                                cover = state.value.cover
                            )
                        )
                        id
                    } else {
                        state.value.artistWithMusics.artist.coverId
                    }

                    artistDao.insertArtist(
                        state.value.artistWithMusics.artist.copy(
                            artistName = state.value.artistWithMusics.artist.artistName.trim(),
                            coverId = coverId
                        )
                    )

                    // On redirige les potentiels albums de l'artiste :
                    val legacyArtistOfAlbums = albumDao.getAllAlbumsWithMusicsSimple().filter {
                        (it.artist!!.artistName == state.value.artistWithMusics.artist.artistName.trim())
                    }

                    /*
                     Si, une fois le nom de l'artiste changé,
                     on a deux albums ayant les mêmes noms d'album et d'artiste,
                     on redirige les musiques de l'album n'ayant pas le même id d'artiste que
                     celui actuel vers l'artiste actuel :
                     */
                    val albumsOrderedByAppearance =
                        legacyArtistOfAlbums.groupingBy { it.album.albumName }.eachCount()

                    for (entry in albumsOrderedByAppearance.entries) {
                        val albumWithMusicToUpdate = legacyArtistOfAlbums.find {
                            (it.album.albumName == entry.key)
                                    && (it.artist!!.artistId != state.value.artistWithMusics.artist.artistId)
                        }
                        if (entry.value == 2) {
                            // Il y a deux fois le même album !
                            // On redirige les musiques de l'album vers le nouvel album avec le bon id d'artiste
                            for (music in albumWithMusicToUpdate!!.musics) {
                                musicAlbumDao.updateAlbumOfMusic(
                                    musicId = music.musicId,
                                    newAlbumId = legacyArtistOfAlbums.find {
                                        (it.album.albumName == entry.key)
                                                && (it.artist!!.artistId == state.value.artistWithMusics.artist.artistId)
                                    }!!.album.albumId
                                )
                            }
                            // On supprime l'ancien album
                            albumDao.deleteAlbum(
                                albumWithMusicToUpdate.album
                            )
                        } else if (albumWithMusicToUpdate != null) {
                            // Sinon, on met à jour l'id de l'artiste
                            albumArtistDao.updateArtistOfAlbum(
                                albumId = albumWithMusicToUpdate.album.albumId,
                                newArtistId = state.value.artistWithMusics.artist.artistId
                            )
                        }
                    }

                    // Vérifions si il n'y a pas deux fois le même nom d'artiste :
                    val possibleDuplicatedArtist: ArtistWithMusics? =
                        artistDao.getPossibleDuplicatedArtistName(
                            artistName = state.value.artistWithMusics.artist.artistName.trim(),
                            artistId = state.value.artistWithMusics.artist.artistId
                        )
                    if (possibleDuplicatedArtist != null) {
                        /*
                        Il y a un doublon !
                        Il faut rediriger l'id des musiques du doublon vers l'id de l'artiste actuel.
                         */
                        Log.d("Doublons !", possibleDuplicatedArtist.musics.size.toString())
                        for (music in possibleDuplicatedArtist.musics) {
                            musicArtistDao.updateArtistOfMusic(
                                musicId = music.musicId,
                                newArtistId = state.value.artistWithMusics.artist.artistId
                            )
                        }

                        // On supprime l'ancien artiste :
                        artistDao.deleteArtist(
                            possibleDuplicatedArtist.artist
                        )
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    // On met à jour les musiques de l'artiste :
                    for (music in state.value.artistWithMusics.musics) {
                        musicDao.insertMusic(
                            music.copy(
                                artist = state.value.artistWithMusics.artist.artistName.trim()
                            )
                        )
                    }
                }
            }
            is ArtistEvent.ArtistFromId -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val artistWithMusics = artistDao.getArtistWithMusicsSimple(event.artistId)
                    val cover = if (artistWithMusics.artist.coverId != null) {
                        imageCoverDao.getCoverOfElement(artistWithMusics.artist.coverId!!)?.cover
                    } else {
                        null
                    }
                    _state.update {
                        it.copy(
                            artistWithMusics = artistWithMusics,
                            cover = cover,
                            hasCoverBeenChanged = false
                        )
                    }
                }
            }
            is ArtistEvent.SetName -> {
                _state.update {
                    it.copy(
                        artistWithMusics = it.artistWithMusics.copy(
                            artist = it.artistWithMusics.artist.copy(
                                artistName = event.name
                            )
                        )
                    )
                }
            }
            is ArtistEvent.SetCover -> {
                _state.update {
                    it.copy(
                        cover = event.cover,
                        hasCoverBeenChanged = true
                    )
                }
            }
            else -> {}
        }
    }
}
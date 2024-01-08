package com.github.soulsearching.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.model.ArtistWithMusics
import com.github.enteraname74.model.ImageCover
import com.github.enteraname74.repository.AlbumArtistRepository
import com.github.enteraname74.repository.AlbumRepository
import com.github.enteraname74.repository.ArtistRepository
import com.github.enteraname74.repository.ImageCoverRepository
import com.github.enteraname74.repository.MusicAlbumRepository
import com.github.enteraname74.repository.MusicArtistRepository
import com.github.enteraname74.repository.MusicRepository
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.SelectedArtistState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * View model for the modify artist screen.
 */
class ModifyArtistViewModel(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val albumRepository: AlbumRepository,
    private val imageCoverRepository: ImageCoverRepository
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
                        imageCoverRepository.insertImageCover(
                            ImageCover(
                                coverId = id,
                                cover = state.value.cover
                            )
                        )
                        id
                    } else {
                        state.value.artistWithMusics.artist.coverId
                    }

                    artistRepository.insertArtist(
                        state.value.artistWithMusics.artist.copy(
                            artistName = state.value.artistWithMusics.artist.artistName.trim(),
                            coverId = coverId
                        )
                    )

                    // On redirige les potentiels albums de l'artiste :
                    val legacyArtistOfAlbums = albumRepository.getAllAlbumsWithMusics().filter {
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
                                musicAlbumRepository.updateAlbumOfMusic(
                                    musicId = music.musicId,
                                    newAlbumId = legacyArtistOfAlbums.find {
                                        (it.album.albumName == entry.key)
                                                && (it.artist!!.artistId == state.value.artistWithMusics.artist.artistId)
                                    }!!.album.albumId
                                )
                            }
                            // On supprime l'ancien album
                            albumRepository.deleteAlbum(
                                albumWithMusicToUpdate.album
                            )
                        } else if (albumWithMusicToUpdate != null) {
                            // Sinon, on met à jour l'id de l'artiste
                            albumArtistRepository.updateArtistOfAlbum(
                                albumId = albumWithMusicToUpdate.album.albumId,
                                newArtistId = state.value.artistWithMusics.artist.artistId
                            )
                        }
                    }

                    // Vérifions si il n'y a pas deux fois le même nom d'artiste :
                    val possibleDuplicatedArtist: ArtistWithMusics? =
                        artistRepository.getPossibleDuplicatedArtist(
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
                            musicArtistRepository.updateArtistOfMusic(
                                musicId = music.musicId,
                                newArtistId = state.value.artistWithMusics.artist.artistId
                            )
                        }

                        // On supprime l'ancien artiste :
                        artistRepository.deleteArtist(
                            possibleDuplicatedArtist.artist
                        )
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    // On met à jour les musiques de l'artiste :
                    for (music in state.value.artistWithMusics.musics) {
                        musicRepository.insertMusic(
                            music.copy(
                                artist = state.value.artistWithMusics.artist.artistName.trim()
                            )
                        )
                    }
                }
            }
            is ArtistEvent.ArtistFromId -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val artistWithMusics = artistRepository.getArtistWithMusics(event.artistId) ?: return@launch
                    val cover = if (artistWithMusics.artist.coverId != null) {
                        imageCoverRepository.getCoverOfElement(artistWithMusics.artist.coverId!!)?.cover
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
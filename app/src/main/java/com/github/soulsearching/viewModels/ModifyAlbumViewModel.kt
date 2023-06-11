package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.ImageCover
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
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ModifyAlbumViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val musicArtistDao: MusicArtistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val albumArtistDao: AlbumArtistDao,
    private val imageCoverDao: ImageCoverDao
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

                    val initialAlbum = albumDao.getAlbumFromId(
                        albumId = state.value.albumWithMusics.album.albumId
                    )!!

                    val initialArtist = artistDao.getArtistFromId(
                        artistId = state.value.albumWithMusics.artist!!.artistId
                    )
                    var currentArtist = initialArtist

                    // Si on a changé l'image de l'album, il faut changer l'id de la couverture :
                    val coverId = if (state.value.hasSetNewCover) {
                        val newCoverId = UUID.randomUUID()
                        imageCoverDao.insertImageCover(
                            ImageCover(
                                coverId = newCoverId,
                                cover = state.value.albumCover
                            )
                        )
                        newCoverId
                    } else {
                        state.value.albumWithMusics.album.coverId
                    }

                    if (state.value.albumWithMusics.artist!!.artistName.trim() != initialArtist!!.artistName) {
                        // On cherche le nouvel artiste correspondant :
                        var newArtist = artistDao.getArtistFromInfo(
                            artistName = state.value.albumWithMusics.artist!!.artistName.trim()
                        )
                        // Si ce nouvel artiste n'existe pas, on le crée :
                        if (newArtist == null) {
                            newArtist = Artist(
                                artistName = state.value.albumWithMusics.artist!!.artistName.trim(),
                                coverId = coverId
                            )
                            artistDao.insertArtist(
                                artist = newArtist
                            )
                        }
                        // On met à jour le lien vers l'artiste :
                        albumArtistDao.updateArtistOfAlbum(
                            albumId = state.value.albumWithMusics.album.albumId,
                            newArtistId = newArtist.artistId
                        )

                        val duplicateAlbum = albumDao.getPossibleDuplicateAlbum(
                            albumId = state.value.albumWithMusics.album.albumId,
                            albumName = state.value.albumWithMusics.album.albumName.trim(),
                            artistId = newArtist.artistId
                        )

                        if (duplicateAlbum != null) {
                            /*
                             Un album a le même nom d'album et d'artiste !
                             On redirige les musiques de l'album dupliqué :
                             */
                            musicAlbumDao.updateMusicsAlbum(
                                newAlbumId = state.value.albumWithMusics.album.albumId,
                                legacyAlbumId = duplicateAlbum.albumId
                            )
                            // On supprime l'ancien album :
                            albumArtistDao.deleteAlbumFromArtist(
                                albumId = duplicateAlbum.albumId
                            )
                            albumDao.deleteAlbum(
                                album = duplicateAlbum
                            )
                        }
                        currentArtist = newArtist
                    } else if (state.value.albumWithMusics.album.albumName.trim() != initialAlbum.albumName) {
                        val duplicateAlbum = albumDao.getPossibleDuplicateAlbum(
                            albumId = state.value.albumWithMusics.album.albumId,
                            albumName = state.value.albumWithMusics.album.albumName.trim(),
                            artistId = state.value.albumWithMusics.artist!!.artistId
                        )

                        if (duplicateAlbum != null) {
                            /*
                             Un album a le même nom d'album et d'artiste !
                             On redirige les musiques de l'album dupliqué :
                             */
                            musicAlbumDao.updateMusicsAlbum(
                                newAlbumId = state.value.albumWithMusics.album.albumId,
                                legacyAlbumId = duplicateAlbum.albumId
                            )
                            // On supprime l'ancien album :
                            albumArtistDao.deleteAlbumFromArtist(
                                albumId = duplicateAlbum.albumId
                            )
                            albumDao.deleteAlbum(
                                album = duplicateAlbum
                            )
                        }
                    }

                    // On met à jour les musiques de l'album :
                    val musicsFromAlbum = musicDao.getMusicsFromAlbum(
                        albumId = state.value.albumWithMusics.album.albumId
                    )
                    for (music in musicsFromAlbum) {
                        // On vérifie si on peut supprimer les anciennes couvertures des musiques :
                        if (music.coverId != null) {
                            Utils.checkAndDeleteCovers(
                                imageCoverDao = imageCoverDao,
                                legacyCoverId = music.coverId!!
                            )
                        }

                        // On modifie les infos de chaque musique
                        musicDao.insertMusic(
                            music.copy(
                                album = state.value.albumWithMusics.album.albumName.trim(),
                                coverId = coverId,
                                artist = state.value.albumWithMusics.artist!!.artistName.trim()
                            )
                        )
                        // Ainsi que leur liens :
                        musicArtistDao.updateArtistOfMusic(
                            musicId = music.musicId,
                            newArtistId = currentArtist!!.artistId
                        )
                    }

                    // On modifie notre album :
                    albumDao.insertAlbum(
                        state.value.albumWithMusics.album.copy(
                            albumName = state.value.albumWithMusics.album.albumName,
                            coverId = coverId
                        )
                    )

                    // On vérifie si l'ancien artiste possède encore des musiques :
                    Utils.checkAndDeleteArtist(
                        artistToCheck = initialArtist,
                        musicArtistDao = musicArtistDao,
                        artistDao = artistDao,
                        imageCoverDao = imageCoverDao
                    )
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
                        albumCover = event.cover,
                        hasSetNewCover = true
                    )
                }
            }
            is AlbumEvent.SetArtist -> {
                _state.update {
                    it.copy(
                        albumWithMusics = it.albumWithMusics.copy(
                            artist = it.albumWithMusics.artist!!.copy(
                                artistName = event.artist
                            )
                        )
                    )
                }
            }
            else -> {}
        }
    }
}
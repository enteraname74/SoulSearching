package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.classes.utils.AndroidUtils
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.states.SelectedAlbumState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * View model for the modify album screen.
 */
class ModifyAlbumViewModel(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val imageCoverRepository: ImageCoverRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SelectedAlbumState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedAlbumState()
    )

    /**
     * Manage album events.
     */
    fun onAlbumEvent(event: AlbumEvent) {
        when (event) {
            AlbumEvent.UpdateAlbum -> {
                CoroutineScope(Dispatchers.IO).launch {

                    val initialAlbum = albumRepository.getAlbumFromId(
                        albumId = state.value.albumWithMusics.album.albumId
                    )!!

                    val initialArtist = artistRepository.getArtistFromId(
                        artistId = state.value.albumWithMusics.artist!!.artistId
                    )
                    var currentArtist = initialArtist

                    // Si on a changé l'image de l'album, il faut changer l'id de la couverture :
                    val coverId = if (state.value.hasSetNewCover) {
                        val newCoverId = UUID.randomUUID()
                        imageCoverRepository.insertImageCover(
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
                        var newArtist = artistRepository.getArtistFromInfo(
                            artistName = state.value.albumWithMusics.artist!!.artistName.trim()
                        )
                        // Si ce nouvel artiste n'existe pas, on le crée :
                        if (newArtist == null) {
                            newArtist = Artist(
                                artistName = state.value.albumWithMusics.artist!!.artistName.trim(),
                                coverId = coverId
                            )
                            artistRepository.insertArtist(
                                artist = newArtist
                            )
                        }
                        // On met à jour le lien vers l'artiste :
                        albumArtistRepository.updateArtistOfAlbum(
                            albumId = state.value.albumWithMusics.album.albumId,
                            newArtistId = newArtist.artistId
                        )

                        val duplicateAlbum = albumRepository.getPossibleDuplicateAlbum(
                            albumId = state.value.albumWithMusics.album.albumId,
                            albumName = state.value.albumWithMusics.album.albumName.trim(),
                            artistId = newArtist.artistId
                        )

                        if (duplicateAlbum != null) {
                            /*
                             Un album a le même nom d'album et d'artiste !
                             On redirige les musiques de l'album dupliqué :
                             */
                            musicAlbumRepository.updateMusicsAlbum(
                                newAlbumId = state.value.albumWithMusics.album.albumId,
                                legacyAlbumId = duplicateAlbum.albumId
                            )
                            // On supprime l'ancien album :
                            albumArtistRepository.deleteAlbumFromArtist(
                                albumId = duplicateAlbum.albumId
                            )
                            albumRepository.deleteAlbum(
                                album = duplicateAlbum
                            )
                        }
                        currentArtist = newArtist
                    } else if (state.value.albumWithMusics.album.albumName.trim() != initialAlbum.albumName) {
                        val duplicateAlbum = albumRepository.getPossibleDuplicateAlbum(
                            albumId = state.value.albumWithMusics.album.albumId,
                            albumName = state.value.albumWithMusics.album.albumName.trim(),
                            artistId = state.value.albumWithMusics.artist!!.artistId
                        )

                        if (duplicateAlbum != null) {
                            /*
                             Un album a le même nom d'album et d'artiste !
                             On redirige les musiques de l'album dupliqué :
                             */
                            musicAlbumRepository.updateMusicsAlbum(
                                newAlbumId = state.value.albumWithMusics.album.albumId,
                                legacyAlbumId = duplicateAlbum.albumId
                            )
                            // On supprime l'ancien album :
                            albumArtistRepository.deleteAlbumFromArtist(
                                albumId = duplicateAlbum.albumId
                            )
                            albumRepository.deleteAlbum(
                                album = duplicateAlbum
                            )
                        }
                    }

                    // On met à jour les musiques de l'album :
                    val musicsFromAlbum = musicRepository.getAllMusicFromAlbum(
                        albumId = state.value.albumWithMusics.album.albumId
                    )
                    for (music in musicsFromAlbum) {
                        // On modifie les infos de chaque musique
                        val newMusic = music.copy(
                            album = state.value.albumWithMusics.album.albumName.trim(),
                            coverId = coverId,
                            artist = state.value.albumWithMusics.artist!!.artistName.trim()
                        )
                        musicRepository.insertMusic(newMusic)
                        // Ainsi que leur liens :
                        musicArtistRepository.updateArtistOfMusic(
                            musicId = music.musicId,
                            newArtistId = currentArtist!!.artistId
                        )

                        PlayerUtils.playerViewModel.updateMusic(newMusic)

                        PlayerUtils.playerViewModel.currentMusic?.let {
                            if (it.musicId.compareTo(music.musicId) == 0) {
                                PlayerUtils.playerViewModel.currentMusicCover = state.value.albumCover
                                PlayerService.updateNotification()
                            }
                        }
                    }

                    // On modifie notre album :
                    albumRepository.insertAlbum(
                        state.value.albumWithMusics.album.copy(
                            albumName = state.value.albumWithMusics.album.albumName.trim(),
                            coverId = coverId
                        )
                    )

                    // On vérifie si l'ancien artiste possède encore des musiques :
                    AndroidUtils.checkAndDeleteArtist(
                        artistToCheck = initialArtist,
                        musicArtistRepository = musicArtistRepository,
                        artistRepository = artistRepository,
                    )
                }
            }
            is AlbumEvent.AlbumFromID -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val albumWithMusics = albumRepository.getAlbumWithMusics(event.albumId)
                    val cover = if (albumWithMusics.album.coverId != null) {
                        imageCoverRepository.getCoverOfElement(albumWithMusics.album.coverId !!)?.cover
                    } else {
                        null
                    }
                    _state.update {
                        it.copy(
                            albumWithMusics = albumWithMusics,
                            albumCover = cover,
                            hasSetNewCover = false
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
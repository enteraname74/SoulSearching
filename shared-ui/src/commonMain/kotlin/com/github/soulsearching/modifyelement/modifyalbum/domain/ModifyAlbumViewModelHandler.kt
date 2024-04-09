package com.github.soulsearching.modifyelement.modifyalbum.domain

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.events.AlbumEvent
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.elementpage.albumpage.domain.SelectedAlbumState
import com.github.soulsearching.domain.utils.Utils
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler for managing the ModifyAlbumViewModel.
 */
class ModifyAlbumViewModelHandler(
    coroutineScope: CoroutineScope,
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val playbackManager: PlaybackManager
) : ViewModelHandler {
    private val _state = MutableStateFlow(SelectedAlbumState())
    val state = _state.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedAlbumState()
    )

    /**
     * Utility method for removing leading and trailing whitespaces in a AlbumWithArtist when modifying its information.
     */
    private fun AlbumWithArtist.trim() = this.copy(
        album = this.album.copy(
            albumName = this.album.albumName.trim()
        ),
        artist = this.artist?.copy(
            artistName = this.artist!!.artistName.trim()
        )
    )

    /**
     * Manage album events.
     */
    fun onAlbumEvent(event: AlbumEvent) {
        when (event) {
            AlbumEvent.UpdateAlbum -> {
                CoroutineScope(Dispatchers.IO).launch {

                    // Si on a changé l'image de l'album, il faut changer l'id de la couverture :
                    val coverId = if (state.value.hasSetNewCover && state.value.albumCover != null) {
                        imageCoverRepository.save(cover = state.value.albumCover!!)
                    } else {
                        state.value.albumWithMusics.album.coverId
                    }

                    val albumWithArtist = _state.value.albumWithMusics.toAlbumWithArtist().trim()
                    val newAlbumWithArtistInformation = albumWithArtist.copy(
                        album = albumWithArtist.album.copy(
                            coverId = coverId
                        )
                    )

                    albumRepository.update(newAlbumWithArtistInformation = newAlbumWithArtistInformation)

                }
            }

            is AlbumEvent.AlbumFromID -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val albumWithMusics = albumRepository.getAlbumWithMusics(event.albumId)
                    val cover = if (albumWithMusics.album.coverId != null) {
                        imageCoverRepository.getCoverOfElement(albumWithMusics.album.coverId!!)?.cover
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
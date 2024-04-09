package com.github.soulsearching.modifyelement.modifyalbum.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager
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
    private val albumRepository: AlbumRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val playbackManager: PlaybackManager
) : ViewModelHandler {
    private val _state = MutableStateFlow(ModifyAlbumState())
    val state = _state.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        ModifyAlbumState()
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
    fun onAlbumEvent(event: ModifyAlbumEvent) {
        when (event) {
            ModifyAlbumEvent.UpdateAlbum -> update()
            is ModifyAlbumEvent.AlbumFromID -> setSelectedAlbum(albumId = event.albumId)
            is ModifyAlbumEvent.SetName -> setAlbum(newAlbumName = event.name)
            is ModifyAlbumEvent.SetCover -> setAlbumCover(cover = event.cover)
            is ModifyAlbumEvent.SetArtist -> setArtist(newArtistName = event.artist)
            else -> {}
        }
    }

    private fun setSelectedAlbum(albumId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val albumWithMusics = albumRepository.getAlbumWithMusics(albumId)

            val cover = if (albumWithMusics.album.coverId != null) {
                imageCoverRepository.getCoverOfElement(albumWithMusics.album.coverId!!)?.cover
            } else {
                null
            }

            _state.update {
                it.copy(
                    albumWithMusics = albumWithMusics,
                    albumCover = cover,
                    hasSetNewCover = false,
                    isSelectedAlbumFetched = true
                )
            }
        }
    }

    /**
     * Set the new cover name to show to the user.
     */
    private fun setAlbumCover(cover: ImageBitmap) {
        _state.update {
            it.copy(
                albumCover = cover,
                hasSetNewCover = true
            )
        }
    }

    /**
     * Set the new album name to show to the user.
     */
    private fun setAlbum(newAlbumName: String) {
        _state.update {
            it.copy(
                albumWithMusics = it.albumWithMusics.copy(
                    album = it.albumWithMusics.album.copy(
                        albumName = newAlbumName
                    )
                )
            )
        }
    }

    /**
     * Set the new artist name to show to the user.
     */
    private fun setArtist(newArtistName: String) {
        _state.update {
            it.copy(
                albumWithMusics = it.albumWithMusics.copy(
                    artist = it.albumWithMusics.artist!!.copy(
                        artistName = newArtistName
                    )
                )
            )
        }
    }

    /**
     * Update the information of the selected album.
     */
    private fun update() {
        CoroutineScope(Dispatchers.IO).launch {
            // If the image has changed, we need to save it and retrieve its id.
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

            // We update the information of the album.
            albumRepository.update(newAlbumWithArtistInformation = newAlbumWithArtistInformation)

            // We retrieve the updated album.
            val newAlbumWithMusics = albumRepository.getAlbumWithMusics(
                albumId = newAlbumWithArtistInformation.album.albumId
            )

            // We need to update the album's songs that are in the played list.
            for (music in newAlbumWithMusics.musics) playbackManager.updateMusic(music)
        }
    }
}
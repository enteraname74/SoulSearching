package com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.usecase.album.GetAlbumWithMusicsUseCase
import com.github.enteraname74.domain.usecase.album.GetAlbumsNameFromSearchString
import com.github.enteraname74.domain.usecase.album.UpdateAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.imagecover.GetCoverOfElementUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ModifyAlbumViewModel(
    private val getAlbumsNameFromSearchString: GetAlbumsNameFromSearchString,
    private val getArtistsNameFromSearchStringUseCase: GetArtistsNameFromSearchStringUseCase,
    private val getAlbumWithMusicsUseCase: GetAlbumWithMusicsUseCase,
    private val getCoverOfElementUseCase: GetCoverOfElementUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updateAlbumUseCase: UpdateAlbumUseCase,
    private val playbackManager: PlaybackManager
) : ScreenModel {
    private val _state = MutableStateFlow(ModifyAlbumState())
    val state = _state.stateIn(
        screenModelScope,
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
    fun onEvent(event: ModifyAlbumEvent) {
        when (event) {
            ModifyAlbumEvent.UpdateAlbum -> update()
            is ModifyAlbumEvent.AlbumFromID -> setSelectedAlbum(albumId = event.albumId)
            is ModifyAlbumEvent.SetName -> setAlbum(newAlbumName = event.name)
            is ModifyAlbumEvent.SetCover -> setAlbumCover(cover = event.cover)
            is ModifyAlbumEvent.SetArtist -> setArtist(newArtistName = event.artist)
            is ModifyAlbumEvent.SetMatchingAlbums -> setMatchingAlbums(search = event.search)
            is ModifyAlbumEvent.SetMatchingArtists -> setMatchingArtists(search = event.search)
        }
    }

    /**
     * Set a list of matching albums names to use when modifying the album field of an album.
     */
    private fun setMatchingAlbums(search: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update {
                it.copy(
                    matchingAlbumsNames = getAlbumsNameFromSearchString(searchString = search)
                )
            }
        }
    }

    /**
     * Set a list of matching artists names to use when modifying the artist field of an album.
     */
    private fun setMatchingArtists(search: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update {
                it.copy(
                    matchingArtistsNames = getArtistsNameFromSearchStringUseCase(searchString = search)
                )
            }
        }
    }

    /**
     * Set the selected album for the modify screen information.
     */
    private fun setSelectedAlbum(albumId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val albumWithMusics: AlbumWithMusics = getAlbumWithMusicsUseCase(albumId).first() ?: return@launch

            val cover: ImageBitmap? = albumWithMusics.album.coverId?.let { coverId ->
                getCoverOfElementUseCase(
                    coverId = coverId
                )?.cover
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
            val coverId = if (_state.value.hasSetNewCover && _state.value.albumCover != null) {
                val imageCover = ImageCover(
                    cover = _state.value.albumCover!!,
                )
                upsertImageCoverUseCase(imageCover = imageCover)
                imageCover.coverId
            } else {
                _state.value.albumWithMusics.album.coverId
            }

            val albumWithArtist = _state.value.albumWithMusics.toAlbumWithArtist().trim()
            val newAlbumWithArtistInformation = albumWithArtist.copy(
                album = albumWithArtist.album.copy(
                    coverId = coverId
                )
            )

            // We update the information of the album.
            updateAlbumUseCase(newAlbumWithArtistInformation = newAlbumWithArtistInformation)

            // We retrieve the updated album
            val newAlbumWithMusics: AlbumWithMusics = getAlbumWithMusicsUseCase(
                albumId = newAlbumWithArtistInformation.album.albumId
            ).first() ?: return@launch

            // We need to update the album's songs that are in the played list.
            for (music in newAlbumWithMusics.musics) playbackManager.updateMusic(music)
        }
    }
}
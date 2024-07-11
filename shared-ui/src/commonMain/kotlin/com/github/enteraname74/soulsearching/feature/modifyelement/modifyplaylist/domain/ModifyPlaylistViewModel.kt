package com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.imagecover.GetCoverOfElementUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ModifyPlaylistViewModel(
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getCoverOfElementUseCase: GetCoverOfElementUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
) : ScreenModel {

    private val _state = MutableStateFlow(ModifyPlaylistState())
    val state = _state.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        ModifyPlaylistState()
    )

    /**
     * Manage playlist events.
     */
    fun onEvent(event: ModifyPlaylistEvent) {
        when(event) {
            is ModifyPlaylistEvent.PlaylistFromId -> setSelectedPlaylist(playlistId = event.playlistId)
            is ModifyPlaylistEvent.SetCover -> setSelectedPlaylistCover(cover = event.cover)
            is ModifyPlaylistEvent.SetName -> setSelectedPlaylistName(newPlaylistName = event.name)
            ModifyPlaylistEvent.UpdatePlaylist -> updatePlaylist()
        }
    }

    /**
     * Utility method for removing leading and trailing whitespaces in playlist information when modifying it.
     */
    private fun Playlist.trim() = this.copy(
        name = this.name.trim()
    )

    /**
     * Update selected playlist information.
     */
    private fun updatePlaylist() {
        CoroutineScope(Dispatchers.IO).launch {
            val coverId = if (_state.value.hasSetNewCover && _state.value.playlistCover != null) {
                val imageCover = ImageCover(
                    cover = _state.value.playlistCover
                )
                upsertImageCoverUseCase(imageCover = imageCover)
                imageCover.coverId
            } else {
                _state.value.selectedPlaylist.coverId
            }

            val newPlaylistInformation = _state.value.selectedPlaylist.trim().copy(
                coverId = coverId
            )

            upsertPlaylistUseCase(
                playlist = newPlaylistInformation,
            )
        }
    }

    /**
     * Set information about the selected playlist and other fields.
     */
    private fun setSelectedPlaylist(playlistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlist = getPlaylistUseCase(playlistId).first() ?: return@launch

            val cover: ImageBitmap? = playlist.coverId?.let { coverId ->
                getCoverOfElementUseCase(
                    coverId = coverId,
                )?.cover
            }

            _state.update {
                it.copy(
                    selectedPlaylist = playlist,
                    playlistCover = cover,
                    hasSetNewCover = false
                )
            }
        }
    }

    /**
     * Set the name of the selected playlist.
     * Primarily used when changing the name of the selected playlist.
     */
    private fun setSelectedPlaylistName(newPlaylistName: String) {
        _state.update {
            it.copy(
                selectedPlaylist = it.selectedPlaylist.copy(
                    name = newPlaylistName
                )
            )
        }
    }

    /**
     * Set the cover of the selected playlist.
     * Primarily used when changing the cover of the selected playlist.
     */
    private fun setSelectedPlaylistCover(cover: ImageBitmap) {
        _state.update {
            it.copy(
                playlistCover = cover,
                hasSetNewCover = true
            )
        }
    }
}
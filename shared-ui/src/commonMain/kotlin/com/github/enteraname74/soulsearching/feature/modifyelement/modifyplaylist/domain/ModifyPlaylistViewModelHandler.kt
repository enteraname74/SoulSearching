package com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.handler.ViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler for managing the ModifyPlaylistViewModel.
 */
class ModifyPlaylistViewModelHandler(
    coroutineScope: CoroutineScope,
    private val playlistRepository : PlaylistRepository,
    private val imageCoverRepository: ImageCoverRepository,
) : ViewModelHandler {

    private val _state = MutableStateFlow(ModifyPlaylistState())
    val state = _state.stateIn(
        coroutineScope,
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
                imageCoverRepository.save(cover = _state.value.playlistCover!!)
            } else {
                _state.value.selectedPlaylist.coverId
            }

            val newPlaylistInformation = _state.value.selectedPlaylist.trim().copy(
                coverId = coverId
            )

            playlistRepository.insertPlaylist(newPlaylistInformation)
        }
    }

    /**
     * Set information about the selected playlist and other fields.
     */
    private fun setSelectedPlaylist(playlistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlist = playlistRepository.getPlaylistFromId(playlistId)

            val cover = if (playlist.coverId != null) {
                imageCoverRepository.getCoverOfElement(playlist.coverId!!)?.cover
            } else {
                null
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
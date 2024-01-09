package com.github.soulsearching.events.handlers

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.classes.settings.SoulSearchingSettings
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Class used to handle playlist events.
 */
class PlaylistEventHandler(
    private val privateState: MutableStateFlow<PlaylistState>,
    private val publicState: StateFlow<PlaylistState>,
    private val sortType: MutableStateFlow<Int> = MutableStateFlow(SortType.NAME),
    private val sortDirection: MutableStateFlow<Int> = MutableStateFlow(SortDirection.ASC),
    private val playlistRepository: PlaylistRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val settings: SoulSearchingSettings
) {

    /**
     * Handle a playlist event.
     */
    fun handleEvent(event: PlaylistEvent) {
        when(event) {
            is PlaylistEvent.BottomSheet -> showOrHidBottomSheet(event)
            is PlaylistEvent.DeleteDialog -> showOrHideDeleteDialog(event)
            is PlaylistEvent.CreatePlaylistDialog -> showOrHideCreatePlaylistDialog(event)
            is PlaylistEvent.AddPlaylist -> addPlaylist(event)
            is PlaylistEvent.AddMusicToPlaylists -> addMusicToPlaylists(event)
            is PlaylistEvent.RemoveMusicFromPlaylist -> removeMusicFromSelectedPlaylist(event)
            is PlaylistEvent.DeletePlaylist -> deleteSelectedPlaylist()
            is PlaylistEvent.SetSelectedPlaylist -> setSelectedPlaylist(event)
            is PlaylistEvent.TogglePlaylistSelectedState -> togglePlaylistSelectedState(event)
            is PlaylistEvent.PlaylistsSelection -> setSelectablePlaylistsForMusic(event)
            is PlaylistEvent.UpdatePlaylist -> updatePlaylist()
            is PlaylistEvent.PlaylistFromId -> setPlaylistInformation(event)
            is PlaylistEvent.SetName -> setSelectedPlaylistName(event)
            is PlaylistEvent.SetCover -> setSelectedPlaylistCover(event)
            is PlaylistEvent.SetSortDirection -> setSortDirection(event)
            is PlaylistEvent.SetSortType -> setSortType(event)
            is PlaylistEvent.CreateFavoritePlaylist -> createFavoritePlaylist(event)
            is PlaylistEvent.UpdateQuickAccessState -> updateQuickAccessState()
            is PlaylistEvent.AddNbPlayed -> incrementNbPlayedOfPlaylist(event)
        }
    }

    /**
     * Show or hide the playlist bottom sheet.
     */
    private fun showOrHidBottomSheet(event: PlaylistEvent.BottomSheet) {
        privateState.update {
            it.copy(
                isBottomSheetShown = event.isShown
            )
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(event: PlaylistEvent.DeleteDialog) {
        privateState.update {
            it.copy(
                isDeleteDialogShown = event.isShown
            )
        }
    }

    /**
     * Show or hide the create playlist dialog.
     */
    private fun showOrHideCreatePlaylistDialog(event: PlaylistEvent.CreatePlaylistDialog) {
        privateState.update {
            it.copy(
                isCreatePlaylistDialogShown = event.isShown
            )
        }
    }

    /**
     * Add a new playlist.
     */
    private fun addPlaylist(event: PlaylistEvent.AddPlaylist) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistRepository.insertPlaylist(
                Playlist(
                    playlistId = UUID.randomUUID(),
                    name = event.name
                )
            )
        }
    }

    /**
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(event: PlaylistEvent.AddMusicToPlaylists) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylistId in publicState.value.multiplePlaylistSelected) {
                musicPlaylistRepository.insertMusicIntoPlaylist(
                    MusicPlaylist(
                        musicId = event.musicId,
                        playlistId = selectedPlaylistId
                    )
                )
            }
            privateState.update {
                it.copy(
                    multiplePlaylistSelected = ArrayList()
                )
            }
        }
    }

    /**
     * Remove a music from the selected playlist
     */
    private fun removeMusicFromSelectedPlaylist(event: PlaylistEvent.RemoveMusicFromPlaylist) {
        CoroutineScope(Dispatchers.IO).launch {
            musicPlaylistRepository.deleteMusicFromPlaylist(
                musicId = event.musicId,
                playlistId = publicState.value.selectedPlaylist.playlistId
            )
        }
    }

    /**
     * Delete the selected playlist
     */
    private fun deleteSelectedPlaylist() {
        CoroutineScope(Dispatchers.IO).launch {
            playlistRepository.deletePlaylist(publicState.value.selectedPlaylist)
        }
    }

    /**
     * Set the selected playlist.
     */
    private fun setSelectedPlaylist(event: PlaylistEvent.SetSelectedPlaylist) {
        privateState.update {
            it.copy(
                selectedPlaylist = event.playlist
            )
        }
    }

    /**
     * Toggle a playlist selection state.
     */
    private fun togglePlaylistSelectedState(event: PlaylistEvent.TogglePlaylistSelectedState) {
        val newList = ArrayList(publicState.value.multiplePlaylistSelected)
        if (event.playlistId in newList) newList.remove(event.playlistId)
        else newList.add(event.playlistId)

        privateState.update {
            it.copy(
                multiplePlaylistSelected = newList
            )
        }
    }

    /**
     * Define the playlists where a music can be added.
     */
    private fun setSelectablePlaylistsForMusic(event: PlaylistEvent.PlaylistsSelection) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlists = playlistRepository.getAllPlaylistsWithMusics()
                .filter { playlistWithMusics ->
                    playlistWithMusics.musics.find { it.musicId == event.musicId } == null
                }
            privateState.update {
                it.copy(
                    multiplePlaylistSelected = ArrayList(),
                    playlistsWithMusics = playlists
                )
            }
        }
    }

    /**
     * Update selected playlist information.
     */
    private fun updatePlaylist() {
        CoroutineScope(Dispatchers.IO).launch {
            val coverId = if (publicState.value.hasSetNewCover) {
                val id = UUID.randomUUID()
                imageCoverRepository.insertImageCover(
                    ImageCover(
                        coverId = id,
                        cover = publicState.value.cover
                    )
                )
                id
            } else {
                publicState.value.selectedPlaylist.coverId
            }
            playlistRepository.insertPlaylist(
                publicState.value.selectedPlaylist.copy(
                    name = publicState.value.name.trim(),
                    coverId = coverId
                )
            )
        }
    }

    /**
     * Set information about the selected playlist and other fields.
     */
    private fun setPlaylistInformation(event: PlaylistEvent.PlaylistFromId) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlist = playlistRepository.getPlaylistFromId(event.playlistId)
            val cover = if (playlist.coverId != null) {
                imageCoverRepository.getCoverOfElement(playlist.coverId!!)?.cover
            } else {
                null
            }
            privateState.update {
                it.copy(
                    selectedPlaylist = playlist,
                    name = playlist.name,
                    cover = cover,
                    hasSetNewCover = false
                )
            }
        }
    }

    /**
     * Set the name of the selected playlist.
     * Primarily used when changing the name of the selected playlist.
     */
    private fun setSelectedPlaylistName(event: PlaylistEvent.SetName) {
        privateState.update {
            it.copy(
                name = event.name
            )
        }
    }

    /**
     * Set the cover of the selected playlist.
     * Primarily used when changing the cover of the selected playlist.
     */
    private fun setSelectedPlaylistCover(event: PlaylistEvent.SetCover) {
        privateState.update {
            it.copy(
                cover = event.cover,
                hasSetNewCover = true
            )
        }
    }

    /**
     * Set the sort direction.
     */
    private fun setSortDirection(event: PlaylistEvent.SetSortDirection) {
        sortDirection.value = event.type
        settings.setInt(
            key = SoulSearchingSettings.SORT_PLAYLISTS_DIRECTION_KEY,
            value = event.type
        )
    }

    /**
     * Set the sort type.
     */
    private fun setSortType(event: PlaylistEvent.SetSortType) {
        sortType.value = event.type
        settings.setInt(
            key = SoulSearchingSettings.SORT_PLAYLISTS_TYPE_KEY,
            value = event.type
        )
    }

    /**
     * Create the favorite playlist.
     */
    private fun createFavoritePlaylist(event: PlaylistEvent.CreateFavoritePlaylist) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistRepository.insertPlaylist(
                Playlist(
                    playlistId = UUID.randomUUID(),
                    name = event.name,
                    isFavorite = true
                )
            )
        }
    }

    /**
     * Update the quick access state of the selected playlist.
     */
    private fun updateQuickAccessState() {
        CoroutineScope(Dispatchers.IO).launch {
            playlistRepository.updateQuickAccessState(
                newQuickAccessState = !publicState.value.selectedPlaylist.isInQuickAccess,
                playlistId = publicState.value.selectedPlaylist.playlistId
            )
        }
    }

    /**
     * Increment the number of time a playlist has been played.
     */
    private fun incrementNbPlayedOfPlaylist(event: PlaylistEvent.AddNbPlayed) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistRepository.updateNbPlayed(
                newNbPlayed = playlistRepository.getNbPlayedOfPlaylist(event.playlistId) + 1,
                playlistId = event.playlistId
            )
        }
    }
}
package com.github.enteraname74.soulsearching.domain.events.handlers

import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.domain.events.MusicEvent
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortDirection
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortType
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handle music events.
 */
open class MusicEventHandler(
    private val privateState: MutableStateFlow<MainPageState>,
    private val musicRepository: MusicRepository,
    private val sortType: MutableStateFlow<Int> = MutableStateFlow(SortType.NAME),
    private val sortDirection: MutableStateFlow<Int> = MutableStateFlow(SortDirection.ASC),
    private val settings: SoulSearchingSettings,
    private val playbackManager: PlaybackManager
) {

    /**
     * handle a music event.
     */
    fun handleEvent(event: MusicEvent) {
        when(event) {
            is MusicEvent.DeleteDialog -> showOrHideDeleteDialog(isShown = event.isShown)
            is MusicEvent.RemoveFromPlaylistDialog -> showOrHideRemoveFromPlaylistDialog(event)
            is MusicEvent.BottomSheet -> showOrHideMusicBottomSheet(event)
            is MusicEvent.AddToPlaylistBottomSheet -> showOrHideAddToPlaylistBottomSheet(event)
            is MusicEvent.SetSelectedMusic -> setSelectedMusic(event)
            is MusicEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is MusicEvent.SetSortType -> setSortType(newSortType = event.type)
            is MusicEvent.SetSortDirection -> setSortDirection(newSortDirection = event.direction)
            is MusicEvent.SetFavorite -> toggleFavoriteState(musicId = event.musicId)
            is MusicEvent.ToggleQuickAccessState -> toggleQuickAccessState(musicId = event.musicId)
            is MusicEvent.AddNbPlayed -> incrementNbPlayedOfMusic(musicId = event.musicId)
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(isShown: Boolean) {
        privateState.update {
            it.copy(
                isDeleteDialogShown = isShown
            )
        }
    }

    /**
     * Show or hide the remove from playlist dialog.
     */
    private fun showOrHideRemoveFromPlaylistDialog(event: MusicEvent.RemoveFromPlaylistDialog) {
        privateState.update {
            it.copy(
                isRemoveFromPlaylistDialogShown = event.isShown
            )
        }
    }

    /**
     * Remove the selected music, from the MusicState, from the application
     */
    private fun deleteMusicFromApp(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.delete(musicId = musicId)
            playbackManager.removeSongFromLists(musicId = musicId)
        }
    }

    /**
     * Define the selected music of the state.
     */
    private fun setSelectedMusic(event: MusicEvent.SetSelectedMusic) {
        privateState.update {
            it.copy(
                selectedMusic = event.music
            )
        }
    }

    /**
     * Show or hide the music bottom sheet.
     */
    private fun showOrHideMusicBottomSheet(event: MusicEvent.BottomSheet) {
        privateState.update {
            it.copy(
                isBottomSheetShown = event.isShown
            )
        }
    }

    /**
     * Show or hide the add to playlist bottom sheet.
     */
    private fun showOrHideAddToPlaylistBottomSheet(event: MusicEvent.AddToPlaylistBottomSheet) {
        privateState.update {
            it.copy(
                isAddToPlaylistBottomSheetShown = event.isShown
            )
        }
    }

    /**
     * Set the sort type.
     */
    private fun setSortType(newSortType: Int) {
        sortType.value = newSortType
        privateState.update {
            it.copy(
                sortType = newSortType
            )
        }
        settings.setInt(
            key = SoulSearchingSettings.SORT_MUSICS_TYPE_KEY,
            value = newSortType
        )
    }

    /**
     * Set the sort direction.
     */
    private fun setSortDirection(newSortDirection: Int) {
        sortDirection.value = newSortDirection
        privateState.update {
            it.copy(
                sortDirection = newSortDirection
            )
        }
        settings.setInt(
            key = SoulSearchingSettings.SORT_MUSICS_DIRECTION_KEY,
            value = newSortDirection
        )
    }

    /**
     * Toggle the favorite status of a given music.
     */
    private fun toggleFavoriteState(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.toggleFavoriteState(musicId = musicId)
        }
    }

    /**
     * Toggle the quick access state of the selected music.
     */
    private fun toggleQuickAccessState(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.toggleQuickAccessState(musicId = musicId)
        }
    }

    /**
     * Increment the number of time a music has been played.
     */
    private fun incrementNbPlayedOfMusic(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.updateNbPlayed(musicId = musicId)
        }
    }
}
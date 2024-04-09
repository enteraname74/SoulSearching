package com.github.soulsearching.domain.events.handlers

import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.events.MusicEvent
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.model.SortDirection
import com.github.soulsearching.mainpage.domain.model.SortType
import com.github.soulsearching.mainpage.domain.state.MusicState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handle music events.
 */
open class MusicEventHandler(
    private val privateState: MutableStateFlow<MusicState>,
    private val publicState: StateFlow<MusicState>,
    private val musicRepository: MusicRepository,
    private val sortType: MutableStateFlow<Int> = MutableStateFlow(SortType.NAME),
    private val sortDirection: MutableStateFlow<Int> = MutableStateFlow(SortDirection.ASC),
    private val settings: SoulSearchingSettings
) {

    /**
     * handle a music event.
     */
    fun handleEvent(event: MusicEvent) {
        when(event) {
            is MusicEvent.DeleteDialog -> showOrHideDeleteDialog(event)
            is MusicEvent.RemoveFromPlaylistDialog -> showOrHideRemoveFromPlaylistDialog(event)
            is MusicEvent.BottomSheet -> showOrHideMusicBottomSheet(event)
            is MusicEvent.AddToPlaylistBottomSheet -> showOrHideAddToPlaylistBottomSheet(event)
            is MusicEvent.SetSelectedMusic -> setSelectedMusic(event)
            is MusicEvent.DeleteMusic -> deleteMusicFromApp()
            is MusicEvent.SetSortType -> setSortType(newSortType = event.type)
            is MusicEvent.SetSortDirection -> setSortDirection(newSortDirection = event.direction)
            is MusicEvent.SetFavorite -> toggleFavoriteState(musicId = event.musicId)
            is MusicEvent.UpdateQuickAccessState -> updateQuickAccessState(musicId = event.musicId)
            is MusicEvent.AddNbPlayed -> incrementNbPlayedOfMusic(musicId = event.musicId)
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(event: MusicEvent.DeleteDialog) {
        privateState.update {
            it.copy(
                isDeleteDialogShown = event.isShown
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
    private fun deleteMusicFromApp() {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.delete(musicId = publicState.value.selectedMusic.musicId)
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
     * Update the quick access state of the selected music.
     */
    private fun updateQuickAccessState(musicId: UUID) {
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
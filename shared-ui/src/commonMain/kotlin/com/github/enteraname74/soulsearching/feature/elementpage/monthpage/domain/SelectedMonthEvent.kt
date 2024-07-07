package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import java.util.UUID

/**
 * Events of the selected month screen.
 */
sealed interface SelectedMonthEvent {
    data class SetSelectedMonth(val month: String): SelectedMonthEvent

    data class SetDeleteMusicDialogVisibility(val isShown: Boolean): SelectedMonthEvent

    data class SetMusicBottomSheetVisibility(val isShown: Boolean): SelectedMonthEvent

    data class SetAddToPlaylistBottomSheetVisibility(val isShown: Boolean): SelectedMonthEvent

    data class DeleteMusic(val musicId: UUID): SelectedMonthEvent

    data class ToggleQuickAccessState(val musicId: UUID): SelectedMonthEvent

    data class AddMusicToPlaylists(val musicId: UUID, val selectedPlaylistsIds: List<UUID>): SelectedMonthEvent

    data class AddNbPlayed(val playlistId: UUID): SelectedMonthEvent
}
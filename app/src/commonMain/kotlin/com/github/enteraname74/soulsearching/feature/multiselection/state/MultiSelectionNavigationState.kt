package com.github.enteraname74.soulsearching.feature.multiselection.state

import java.util.UUID

// TODO BOTTOM SHEETS: Add all other bottom sheets
sealed interface MultiSelectionNavigationState {
    data object Idle : MultiSelectionNavigationState
    data class ToMusicBottomSheet(
        val musicIds: List<UUID>,
        val playlistId: UUID?,
    ) : MultiSelectionNavigationState
}
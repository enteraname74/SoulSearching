package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetail

/**
 * State for managing a selected month.
 */
sealed interface SelectedMonthState {
    data class Data(
        val playlistDetail: PlaylistDetail,
        val allPlaylists: List<PlaylistWithMusics> = emptyList(),
    ): SelectedMonthState
    data object Loading: SelectedMonthState
}
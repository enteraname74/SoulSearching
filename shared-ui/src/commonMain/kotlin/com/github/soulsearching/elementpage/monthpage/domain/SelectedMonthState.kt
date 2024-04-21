package com.github.soulsearching.elementpage.monthpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.soulsearching.domain.model.MonthMusicList

/**
 * State for managing a selected month.
 */
data class SelectedMonthState(
    val playlistWithMusics: MonthMusicList? = MonthMusicList(),
    val allPlaylists: List<PlaylistWithMusics> = emptyList(),
    val isDeleteMusicDialogShown: Boolean = false,
    val isMusicBottomSheetShown: Boolean = false,
    val isAddToPlaylistBottomSheetShown: Boolean = false,
    val isRemoveFromPlaylistDialogShown: Boolean = false
)
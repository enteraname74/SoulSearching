package com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.domain.model.MusicFolder

/**
 * State for managing a selected folder.
 */
data class SelectedFolderState(
    val musicFolder: MusicFolder? = MusicFolder(),
    val allPlaylists: List<PlaylistWithMusics> = emptyList(),
    val isDeleteMusicDialogShown: Boolean = false,
    val isMusicBottomSheetShown: Boolean = false,
    val isAddToPlaylistBottomSheetShown: Boolean = false,
    val isRemoveFromPlaylistDialogShown: Boolean = false
)
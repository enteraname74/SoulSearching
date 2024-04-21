package com.github.soulsearching.elementpage.folderpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.soulsearching.domain.model.MusicFolder

/**
 * State for managing a selected folder.
 */
data class SelectedFolderState(
    val playlistWithMusics: MusicFolder? = MusicFolder(),
    val allPlaylists: List<PlaylistWithMusics> = emptyList(),
    val isDeleteMusicDialogShown: Boolean = false,
    val isMusicBottomSheetShown: Boolean = false,
    val isAddToPlaylistBottomSheetShown: Boolean = false,
    val isRemoveFromPlaylistDialogShown: Boolean = false
)
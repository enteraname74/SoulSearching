package com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetail

/**
 * State for managing a selected folder.
 */
sealed interface SelectedFolderState {
    data class Data(
        val playlistDetail: PlaylistDetail,
        val allPlaylists: List<PlaylistWithMusics>,
    ) : SelectedFolderState
    data object Loading: SelectedFolderState
}
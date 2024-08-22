package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain

import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail

/**
 * State for managing a selected folder.
 */
sealed interface SelectedFolderState {
    data class Data(
        val playlistDetail: PlaylistDetail,
    ) : SelectedFolderState
    data object Loading: SelectedFolderState
}
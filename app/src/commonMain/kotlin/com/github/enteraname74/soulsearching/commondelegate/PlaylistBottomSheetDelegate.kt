package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.PlaylistWithMusics

interface PlaylistBottomSheetDelegate {
    fun showPlaylistBottomSheet(selectedPlaylist: PlaylistWithMusics)
}
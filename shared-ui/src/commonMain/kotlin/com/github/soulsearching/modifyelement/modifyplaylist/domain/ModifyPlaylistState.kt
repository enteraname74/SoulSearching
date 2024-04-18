package com.github.soulsearching.modifyelement.modifyplaylist.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Playlist

/**
 * UI State of the modify playlisr screen.
 */
data class ModifyPlaylistState(
    val selectedPlaylist: Playlist = Playlist(),
    val hasSetNewCover: Boolean = false,
    val playlistCover: ImageBitmap? = null
)

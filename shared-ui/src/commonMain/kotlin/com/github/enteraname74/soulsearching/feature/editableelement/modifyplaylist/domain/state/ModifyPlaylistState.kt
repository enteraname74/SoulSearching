package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

/**
 * UI State of the modify playlist screen.
 */
sealed interface ModifyPlaylistState {
    data class Data(
        val initialPlaylist: Playlist,
        val editableElement: EditableElement,
    ): ModifyPlaylistState
    data object Loading: ModifyPlaylistState
}

package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

/**
 * UI State of the modify playlist screen.
 */
sealed interface ModifyPlaylistState {
    data class Data(
        val initialPlaylist: PlaylistWithMusics,
        val editableElement: EditableElement,
    ): ModifyPlaylistState
    data object Loading: ModifyPlaylistState
}

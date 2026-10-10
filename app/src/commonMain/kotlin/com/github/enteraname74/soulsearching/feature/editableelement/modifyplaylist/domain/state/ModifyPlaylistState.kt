package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state

import com.github.enteraname74.soulsearching.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode

/**
 * UI State of the modify playlist screen.
 */
sealed interface ModifyPlaylistState {
    data class Data(
        val initialPlaylist: PlaylistWithMusics,
        val coverEditMode: CoverEditMode,
    ) : ModifyPlaylistState

    data object Loading : ModifyPlaylistState
}

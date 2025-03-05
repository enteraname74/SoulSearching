package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

/**
 * UI state of the modify album screen.
 */
sealed interface ModifyAlbumState {
    data object Loading : ModifyAlbumState
    data class Data(
        val initialAlbum: AlbumWithMusics,
        val editableElement: EditableElement,
    ): ModifyAlbumState
}
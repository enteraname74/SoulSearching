package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

/**
 * UI state of the modify artist screen.
 */
sealed interface ModifyArtistState {
    data object Loading: ModifyArtistState
    data class Data(
        val initialArtist: ArtistWithMusics,
        val editableElement: EditableElement,
    ): ModifyArtistState
}

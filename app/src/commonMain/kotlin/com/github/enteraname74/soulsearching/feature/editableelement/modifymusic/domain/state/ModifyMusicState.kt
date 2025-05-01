package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

/**
 * UI state of the modify music screen.
 */
sealed interface ModifyMusicState {
    data object Loading: ModifyMusicState
    data class Data(
        val initialMusic: Music,
        val editableElement: EditableElement,
    ): ModifyMusicState
}

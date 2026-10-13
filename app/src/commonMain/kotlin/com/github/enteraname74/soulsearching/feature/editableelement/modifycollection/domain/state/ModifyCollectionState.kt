package com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state

import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

sealed interface ModifyCollectionState {
    data class Data(
        val initialCollection: CollectionWithMusics,
        val editableElement: EditableElement,
    ) : ModifyCollectionState

    data object Loading : ModifyCollectionState
}

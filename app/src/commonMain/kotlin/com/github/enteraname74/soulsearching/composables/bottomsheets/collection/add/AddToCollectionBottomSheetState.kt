package com.github.enteraname74.soulsearching.composables.bottomsheets.collection.add

import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import kotlin.uuid.Uuid

data class AddToCollectionBottomSheetState(
    val dialogState: SoulDialog? = null,
    val selectedCollectionIds: Set<Uuid> = emptySet(),
    val collections: List<CollectionPreview> = emptyList(),
)

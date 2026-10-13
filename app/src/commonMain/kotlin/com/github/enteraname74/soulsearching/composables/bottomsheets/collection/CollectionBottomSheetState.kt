package com.github.enteraname74.soulsearching.composables.bottomsheets.collection

import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics

data class CollectionBottomSheetState(
    val collections: List<CollectionWithMusics> = emptyList(),
    val bottomSheetTopInformation: BottomSheetTopInformation = BottomSheetTopInformation(),
    val rowSpecs: List<BottomSheetRowSpec> = emptyList(),
    val dialogState: SoulDialog? = null,
)

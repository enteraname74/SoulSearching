package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog

data class AlbumBottomSheetState(
    val albums: List<AlbumWithMusics> = emptyList(),
    val bottomSheetTopInformation: BottomSheetTopInformation = BottomSheetTopInformation(),
    val rowSpecs: List<BottomSheetRowSpec> = emptyList(),
    val dialogState: SoulDialog? = null,
)

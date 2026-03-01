package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog

data class ArtistBottomSheetState(
    val artists: List<ArtistWithMusics> = emptyList(),
    val bottomSheetTopInformation: BottomSheetTopInformation = BottomSheetTopInformation(),
    val rowSpecs: List<BottomSheetRowSpec> = emptyList(),
    val dialogState: SoulDialog? = null,
)

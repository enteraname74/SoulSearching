package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog

data class PlaylistBottomSheetState(
    val playlists: List<PlaylistWithMusics> = emptyList(),
    val bottomSheetTopInformation: BottomSheetTopInformation = BottomSheetTopInformation(),
    val rowSpecs: List<BottomSheetRowSpec> = emptyList(),
    val dialogState: SoulDialog? = null,
)

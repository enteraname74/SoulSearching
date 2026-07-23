package com.github.enteraname74.soulsearching.composables.bottomsheets.folder

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog

data class FolderBottomSheetState(
    val folders: List<MusicFolderPreview> = emptyList(),
    val musics: List<Music> = emptyList(),
    val bottomSheetTopInformation: BottomSheetTopInformation = BottomSheetTopInformation(),
    val rowSpecs: List<BottomSheetRowSpec> = emptyList(),
    val dialogState: SoulDialog? = null,
)

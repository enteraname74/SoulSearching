package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog

data class MusicBottomSheetState(
    val musics: List<Music> = emptyList(),
    val bottomSheetTopInformation: BottomSheetTopInformation = BottomSheetTopInformation(),
    val itemsVisibility: MusicBottomSheetItemsVisibility = MusicBottomSheetItemsVisibility(),
    val bottomSheetState: SoulBottomSheet? = null,
    val dialogState: SoulDialog? = null,
)

data class MusicBottomSheetItemsVisibility(
    val removeFromPlayedList: Boolean = false,
    val queueActions: Boolean = false,
    val isInQuickAccess: Boolean = false,
    val editEnabled: Boolean = false,
    val inPlaylist: Boolean = false,
)
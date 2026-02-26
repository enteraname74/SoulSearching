package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetMode

data class MusicBottomSheetState(
    val selectedMusic: Music? = null,
    val isCurrentlyPlaying: Boolean = false,
    val isInPlayedList: Boolean = true,
    val mode: MusicBottomSheetMode = MusicBottomSheetMode.NORMAL,
    val bottomSheetState: SoulBottomSheet? = null,
    val dialogState: SoulDialog? = null,
)

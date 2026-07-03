package com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlin.uuid.Uuid

data class AddToPlaylistBottomSheetState(
    val dialogState: SoulDialog? = null,
    val selectedPlaylistIds: Set<Uuid> = emptySet(),
    val playlistsWithMusics: List<PlaylistWithMusics> = emptyList(),
)

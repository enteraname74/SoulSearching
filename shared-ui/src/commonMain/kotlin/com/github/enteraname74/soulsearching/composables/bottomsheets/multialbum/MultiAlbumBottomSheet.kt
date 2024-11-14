package com.github.enteraname74.soulsearching.composables.bottomsheets.multialbum

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import java.util.UUID

class MultiAlbumBottomSheet(
    private val onClose: () -> Unit,
    private val selectedIds: List<UUID>,
    private val onDelete: () -> Unit,
): SoulBottomSheet {
    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose,
        ) { _ ->
            Content()
        }
    }

    @Composable
    private fun Content() {
        MultiAlbumBottomSheetMenu(
            total = selectedIds.size,
            deleteAction = onDelete,
        )
    }
}
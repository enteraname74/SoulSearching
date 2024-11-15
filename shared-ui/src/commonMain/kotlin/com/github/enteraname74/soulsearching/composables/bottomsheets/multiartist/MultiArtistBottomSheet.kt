package com.github.enteraname74.soulsearching.composables.bottomsheets.multiartist

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import java.util.*

class MultiArtistBottomSheet(
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
        MultiArtistBottomSheetMenu(
            total = selectedIds.size,
            deleteAction = onDelete,
        )
    }
}
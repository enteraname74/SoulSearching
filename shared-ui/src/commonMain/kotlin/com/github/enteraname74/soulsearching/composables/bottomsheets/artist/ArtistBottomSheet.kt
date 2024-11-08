package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler

class ArtistBottomSheet(
    private val onClose: () -> Unit,
    private val selectedArtist: ArtistWithMusics,
    private val onModifyArtist: () -> Unit,
    private val onDeleteArtist: () -> Unit,
    private val toggleQuickAccess: () -> Unit,
): SoulBottomSheet {
    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose
        ) { closeWithAnim ->
            Content(closeWithAnim = closeWithAnim)
        }
    }

    @Composable
    private fun Content(
        closeWithAnim: () -> Unit,
    ) {
        ArtistBottomSheetMenu(
            modifyAction = {
                closeWithAnim()
                onModifyArtist()
            },
            deleteAction = onDeleteArtist,
            quickAccessAction = {
                closeWithAnim()
                toggleQuickAccess()
            },
            isInQuickAccess = selectedArtist.isInQuickAccess,
            selectedArtist = selectedArtist,
        )
    }
}
package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler

class AlbumBottomSheet(
    private val onClose: () -> Unit,
    private val selectedAlbum: AlbumWithMusics,
    private val onModifyAlbum: () -> Unit,
    private val onDeleteAlbum: () -> Unit,
    private val toggleQuickAccess: () -> Unit,
): SoulBottomSheet {

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose,
        ) { closeWithAnim ->
            Content(closeWithAnim = closeWithAnim)
        }
    }

    @Composable
    private fun Content(
        closeWithAnim: () -> Unit,
    ) {
        AlbumBottomSheetMenu(
            modifyAction = {
                closeWithAnim()
                onModifyAlbum()
            },
            deleteAction = onDeleteAlbum,
            quickAccessAction = {
               closeWithAnim()
                toggleQuickAccess()
            },
            isInQuickAccess = selectedAlbum.isInQuickAccess,
            selectedAlbum = selectedAlbum,
        )
    }
}
package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetColors
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlbumBottomSheet(
    private val onClose: () -> Unit,
    private val selectedAlbum: Album,
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
            isInQuickAccess = selectedAlbum.isInQuickAccess
        )
    }
}
package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler


class PlaylistBottomSheet(
    private val onClose: () -> Unit,
    private val selectedPlaylist: Playlist,
    private val onModifyPlaylist: () -> Unit,
    private val onDeletePlaylist: () -> Unit,
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
        PlaylistBottomSheetMenu(
            isFavoritePlaylist = selectedPlaylist.isFavorite,
            modifyAction = {
                closeWithAnim()
                onModifyPlaylist()
            },
            deleteAction = onDeletePlaylist,
            quickAccessAction = {
                closeWithAnim()
                toggleQuickAccess()
            },
            isInQuickAccess = selectedPlaylist.isInQuickAccess
        )
    }
}
package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler


class PlaylistBottomSheet(
    private val onClose: () -> Unit,
    private val selectedPlaylist: PlaylistWithMusicsNumber,
    private val onModifyPlaylist: () -> Unit,
    private val onPlayNext: () -> Unit,
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
            selectedPlaylist = selectedPlaylist,
            modifyAction = {
                closeWithAnim()
                onModifyPlaylist()
            },
            deleteAction = onDeletePlaylist,
            quickAccessAction = {
                closeWithAnim()
                toggleQuickAccess()
            },
            isInQuickAccess = selectedPlaylist.isInQuickAccess,
            playNextAction = onPlayNext,
        )
    }
}
package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings

class DeletePlaylistDialog(
    private val playlistToDelete: Playlist,
    private val onDelete: () -> Unit,
    private val onClose: () -> Unit,
): SoulDialog {

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction = {
                onDelete()
            },
            dismissAction = onClose,
            title = strings.deletePlaylistDialogTitle,
            confirmText = strings.delete,
            dismissText = strings.cancel
        )
    }
}
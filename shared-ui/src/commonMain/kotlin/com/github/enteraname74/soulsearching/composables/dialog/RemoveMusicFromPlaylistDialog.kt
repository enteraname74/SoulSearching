package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings

class RemoveMusicFromPlaylistDialog(
    private val onClose: () -> Unit,
    private val onConfirm: () -> Unit,
): SoulDialog {

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            title = strings.removeMusicFromPlaylistTitle,
            text = strings.removeMusicFromPlaylistText,
            confirmAction = onConfirm,
            dismissAction = onClose,
            confirmText = strings.delete,
            dismissText = strings.cancel,
        )
    }
}

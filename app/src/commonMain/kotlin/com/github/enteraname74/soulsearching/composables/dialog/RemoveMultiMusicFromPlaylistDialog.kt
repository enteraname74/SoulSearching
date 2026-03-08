package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings

class RemoveMultiMusicFromPlaylistDialog(
    private val onClose: () -> Unit,
    private val onConfirm: () -> Unit,
): SoulDialog {

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            title = strings.removeSelectedMusicFromPlaylistTitle,
            text = strings.removeSelectedMusicFromPlaylistText,
            confirmAction = onConfirm,
            dismissAction = onClose,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            icon = {
                SoulIcon(
                    icon = CoreRes.drawable.ic_delete_filled,
                    contentDescription = strings.delete,
                )
            }
        )
    }
}
package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

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
            icon = {
                Image(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = strings.delete,
                    colorFilter = ColorFilter.tint(
                        SoulSearchingColorTheme.colorScheme.onPrimary
                    )
                )
            }
        )
    }
}

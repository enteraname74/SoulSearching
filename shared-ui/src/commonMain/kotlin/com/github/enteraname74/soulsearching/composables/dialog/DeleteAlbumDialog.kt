package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun DeleteAlbumDialog(
    onDeleteAlbum: () -> Unit,
    onDismiss: () -> Unit,
) {
    SoulAlertDialog(
        confirmAction = onDeleteAlbum,
        dismissAction =  onDismiss,
        confirmText = strings.delete,
        dismissText = strings.cancel,
        title = strings.deleteAlbumDialogTitle
    )
}
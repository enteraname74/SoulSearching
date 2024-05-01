package com.github.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.soulsearching.domain.events.AlbumEvent
import com.github.soulsearching.strings.strings

@Composable
fun DeleteAlbumDialog(
    onDeleteAlbum: () -> Unit,
    onDismiss: () -> Unit,
) {
    SoulSearchingDialog(
        confirmAction = onDeleteAlbum,
        dismissAction =  onDismiss,
        confirmText = strings.delete,
        dismissText = strings.cancel,
        title = strings.deleteAlbumDialogTitle
    )
}
package com.github.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.soulsearching.domain.events.AlbumEvent
import com.github.soulsearching.strings.strings

@Composable
fun DeleteAlbumDialog(
    onAlbumEvent: (AlbumEvent) -> Unit,
    confirmAction : () -> Unit
) {
    SoulSearchingDialog(
        confirmAction = {
            onAlbumEvent(AlbumEvent.DeleteAlbum)
            onAlbumEvent(AlbumEvent.DeleteDialog(isShown = false))
            confirmAction()
        },
        dismissAction =  {
            onAlbumEvent(AlbumEvent.DeleteDialog(isShown = false))
        },
        confirmText = strings.delete,
        dismissText = strings.cancel,
        title = strings.deleteAlbumDialogTitle
    )
}
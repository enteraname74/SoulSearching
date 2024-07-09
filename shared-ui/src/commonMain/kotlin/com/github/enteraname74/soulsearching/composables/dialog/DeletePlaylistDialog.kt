package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.events.PlaylistEvent
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun DeletePlaylistDialog(
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    confirmAction: () -> Unit
) {
    SoulDialog(
        confirmAction = {
            onPlaylistEvent(PlaylistEvent.DeletePlaylist)
            onPlaylistEvent(PlaylistEvent.DeleteDialog(isShown = false))
            confirmAction()
        },
        dismissAction = {
            onPlaylistEvent(PlaylistEvent.DeleteDialog(isShown = false))
        },
        title = strings.deletePlaylistDialogTitle,
        confirmText = strings.delete,
        dismissText = strings.cancel
    )
}
package com.github.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.strings.strings

@Composable
fun DeletePlaylistDialog(
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    confirmAction: () -> Unit
) {
    SoulSearchingDialog(
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
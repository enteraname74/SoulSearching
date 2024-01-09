package com.github.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.strings

@Composable
fun DeleteArtistDialog(
    onArtistEvent: (ArtistEvent) -> Unit,
    confirmAction : () -> Unit
) {
    SoulSearchingDialog(
        confirmAction = {
            onArtistEvent(ArtistEvent.DeleteArtist)
            onArtistEvent(ArtistEvent.DeleteDialog(isShown = false))
            confirmAction()
        },
        dismissAction = {
            onArtistEvent(ArtistEvent.DeleteDialog(isShown = false))
        },
        confirmText = strings.delete,
        dismissText = strings.cancel,
        title = strings.deleteArtistDialogTitle
    )
}
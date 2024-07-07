package com.github.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.soulsearching.domain.events.ArtistEvent
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun DeleteArtistDialog(
    onArtistEvent: (ArtistEvent) -> Unit,
    confirmAction : () -> Unit
) {
    SoulDialog(
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
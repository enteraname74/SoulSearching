package com.github.soulsearching.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.R
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.theme.DynamicColor

@Composable
fun DeleteArtistDialog(
    onArtistEvent: (ArtistEvent) -> Unit,
    confirmAction : () -> Unit
) {
    AlertDialog(
        onDismissRequest = {onArtistEvent(ArtistEvent.DeleteDialog(isShown = false))},
        confirmButton = {
            TextButton(onClick = {
                onArtistEvent(ArtistEvent.DeleteArtist)
                onArtistEvent(ArtistEvent.DeleteDialog(isShown = false))
                confirmAction()
            }) {
                Text(
                    text = stringResource(id = R.string.delete),
                    color = DynamicColor.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onArtistEvent(ArtistEvent.DeleteDialog(isShown = false)) }) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = DynamicColor.onPrimary
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.delete_artist_dialog_title),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        icon = {
            Image(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(id = R.string.delete),
                colorFilter = ColorFilter.tint(DynamicColor.onPrimary)
            )
        },
        containerColor = DynamicColor.primary,
        textContentColor = DynamicColor.onPrimary,
        titleContentColor = DynamicColor.onPrimary,
        iconContentColor = DynamicColor.onPrimary
    )
}
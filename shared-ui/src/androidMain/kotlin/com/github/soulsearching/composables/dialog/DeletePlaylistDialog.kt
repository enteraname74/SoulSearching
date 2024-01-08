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
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun DeletePlaylistDialog(
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    confirmAction : () -> Unit
) {
    AlertDialog(
        onDismissRequest = {onPlaylistEvent(PlaylistEvent.DeleteDialog(isShown = false))},
        confirmButton = {
            TextButton(onClick = {
                onPlaylistEvent(PlaylistEvent.DeletePlaylist)
                onPlaylistEvent(PlaylistEvent.DeleteDialog(isShown = false))
                confirmAction()
            }) {
                Text(
                    text = stringResource(id = R.string.delete),
                    color = DynamicColor.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onPlaylistEvent(PlaylistEvent.DeleteDialog(isShown = false)) }) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = DynamicColor.onPrimary
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.delete_playlist_dialog_title),
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
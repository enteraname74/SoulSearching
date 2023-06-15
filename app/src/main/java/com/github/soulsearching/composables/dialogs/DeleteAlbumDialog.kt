package com.github.soulsearching.composables.dialogs

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.R
import com.github.soulsearching.events.AlbumEvent

@Composable
fun DeleteAlbumDialog(
    onAlbumEvent: (AlbumEvent) -> Unit,
    confirmAction : () -> Unit
) {
    AlertDialog(
        onDismissRequest = {onAlbumEvent(AlbumEvent.DeleteDialog(isShown = false))},
        confirmButton = {
            TextButton(onClick = {
                onAlbumEvent(AlbumEvent.DeleteAlbum)
                onAlbumEvent(AlbumEvent.DeleteDialog(isShown = false))
                confirmAction()
            }) {
                Text(
                    text = stringResource(id = R.string.delete),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onAlbumEvent(AlbumEvent.DeleteDialog(isShown = false)) }) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.delete_album_dialog_title),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        icon = {
            Image(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.delete),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        },
        containerColor = MaterialTheme.colorScheme.primary,
        textContentColor = MaterialTheme.colorScheme.onPrimary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        iconContentColor = MaterialTheme.colorScheme.onPrimary
    )
}
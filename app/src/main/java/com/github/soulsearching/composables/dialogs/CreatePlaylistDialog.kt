package com.github.soulsearching.composables.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppTextField
import com.github.soulsearching.events.PlaylistEvent

@Composable
fun CreatePlaylistDialog(
    onPlaylistEvent : (PlaylistEvent) -> Unit
) {

    var playlistName by rememberSaveable {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = {onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false))},
        confirmButton = {
            TextButton(onClick = {
                onPlaylistEvent(PlaylistEvent.AddPlaylist(playlistName.trim()))
                onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false))
            }) {
                Text(
                    text = stringResource(id = R.string.create),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false)) }) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.create_playlist_dialog_title),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            AppTextField(
                value = playlistName,
                onValueChange = {playlistName = it},
                labelName = stringResource(id = R.string.playlist_name),
                focusManager = focusManager
            )
        },
        containerColor = MaterialTheme.colorScheme.primary,
        textContentColor = MaterialTheme.colorScheme.onPrimary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        iconContentColor = MaterialTheme.colorScheme.onPrimary
    )
}
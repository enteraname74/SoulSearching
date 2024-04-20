package com.github.soulsearching.mainpage.presentation.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.AppTextField
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.strings.strings

@Composable
fun CreatePlaylistDialog(
    onPlaylistEvent: (PlaylistEvent) -> Unit
) {
    var playlistName by rememberSaveable {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = { onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false)) },
        confirmButton = {
            TextButton(onClick = {
                onPlaylistEvent(PlaylistEvent.AddPlaylist(playlistName.trim()))
                onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false))
            }) {
                Text(
                    text = strings.create,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false)) }) {
                Text(
                    text = strings.cancel,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary
                )
            }
        },
        title = {
            Text(
                text = strings.createPlaylistDialogTitle,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = SoulSearchingColorTheme.colorScheme.onPrimary
            )
        },
        text = {
            AppTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                labelName = strings.playlistName,
                focusManager = focusManager
            )
        },
        containerColor = SoulSearchingColorTheme.colorScheme.primary,
        textContentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        titleContentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        iconContentColor = SoulSearchingColorTheme.colorScheme.onPrimary
    )
}
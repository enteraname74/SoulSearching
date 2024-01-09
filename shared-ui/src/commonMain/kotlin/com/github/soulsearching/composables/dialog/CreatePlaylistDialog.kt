package com.github.soulsearching.composables.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.AppTextField
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.strings
import com.github.soulsearching.theme.DynamicColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreatePlaylistDialog(
    onPlaylistEvent: (PlaylistEvent) -> Unit
) {

    var playlistName by rememberSaveable {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        shape = RoundedCornerShape(Constants.Spacing.veryLarge),
        onDismissRequest = { onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false)) },
        confirmButton = {
            TextButton(
                modifier = Modifier.padding(
                    bottom = Constants.Spacing.veryLarge,
                    end = Constants.Spacing.large
                ),
                onClick = {
                    onPlaylistEvent(PlaylistEvent.AddPlaylist(playlistName.trim()))
                    onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false))
                }) {
                Text(
                    text = strings.create,
                    color = DynamicColor.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.padding(
                    bottom = Constants.Spacing.veryLarge
                ),
                onClick = {
                    onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false))
                }) {
                Text(
                    text = strings.cancel,
                    color = DynamicColor.onPrimary
                )
            }
        },
        title = {
            Text(
                modifier = Modifier.padding(
                    horizontal = Constants.Spacing.medium,
                    vertical = Constants.Spacing.veryLarge
                ),
                text = strings.createPlaylistDialogTitle,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = DynamicColor.onPrimary
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
        backgroundColor = DynamicColor.primary,
        contentColor = DynamicColor.onPrimary
    )
}
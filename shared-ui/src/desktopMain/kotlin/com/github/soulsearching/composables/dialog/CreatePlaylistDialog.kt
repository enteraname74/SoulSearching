package com.github.soulsearching.composables.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.AppTextField
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme

@Composable
actual fun CreatePlaylistDialog(
    onPlaylistEvent: (PlaylistEvent) -> Unit
) {

    var playlistName by rememberSaveable {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    Dialog(
        onDismissRequest = { onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false)) }
    ) {
        Card(
            shape = RoundedCornerShape(Constants.Spacing.veryLarge),
            colors = CardDefaults.cardColors(
                containerColor = SoulSearchingColorTheme.colorScheme.primary,
                contentColor = SoulSearchingColorTheme.colorScheme.onPrimary
            )
        ) {
            Column(
                modifier = Modifier.padding(Constants.Spacing.large),
                verticalArrangement = Arrangement.spacedBy(Constants.Spacing.large),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = strings.createPlaylistDialogTitle,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary
                    )
                    AppTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        labelName = strings.playlistName,
                        focusManager = focusManager
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        modifier = Modifier.padding(
                            end = Constants.Spacing.large
                        ),
                        onClick = {
                            onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false))
                        }) {
                        Text(
                            text = strings.cancel,
                            color = SoulSearchingColorTheme.colorScheme.onPrimary
                        )
                    }
                    TextButton(
                        onClick = {
                            onPlaylistEvent(PlaylistEvent.AddPlaylist(playlistName.trim()))
                            onPlaylistEvent(PlaylistEvent.CreatePlaylistDialog(isShown = false))
                        }) {
                        Text(
                            text = strings.create,
                            color = SoulSearchingColorTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
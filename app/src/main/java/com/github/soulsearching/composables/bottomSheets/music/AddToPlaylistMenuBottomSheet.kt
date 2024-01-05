package com.github.soulsearching.composables.bottomSheets.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlaylistSelectableComposable
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun AddToPlaylistMenuBottomSheet(
    playlistState: PlaylistState,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    cancelAction: () -> Unit,
    validationAction: () -> Unit,
    primaryColor: Color = DynamicColor.secondary,
    textColor: Color = DynamicColor.onSecondary
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(primaryColor)
                .padding(it)
                .padding(Constants.Spacing.medium)
        ) {
            AppHeaderBar(
                title = stringResource(id = R.string.add_to_playlist),
                leftAction = cancelAction,
                rightAction = validationAction,
                rightIcon = Icons.Rounded.Done,
                backgroundColor = Color.Transparent,
                contentColor = textColor
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(playlistState.playlistsWithMusics) { playlistWithMusics ->
                    if (playlistWithMusics.playlist.playlistId in playlistState.multiplePlaylistSelected) {
                        PlaylistSelectableComposable(
                            playlist = playlistWithMusics.playlist,
                            onClick = {
                                onPlaylistEvent(
                                    PlaylistEvent.TogglePlaylistSelectedState(
                                        playlistWithMusics.playlist.playlistId
                                    )
                                )
                            },
                            isSelected = true,
                            textColor = textColor
                        )
                    } else {
                        PlaylistSelectableComposable(
                            playlist = playlistWithMusics.playlist,
                            onClick = {
                                onPlaylistEvent(
                                    PlaylistEvent.TogglePlaylistSelectedState(
                                        playlistWithMusics.playlist.playlistId
                                    )
                                )
                            },
                            isSelected = false,
                            textColor = textColor
                        )
                    }
                }
            }
        }
    }
}
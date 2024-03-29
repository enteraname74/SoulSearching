package com.github.soulsearching.composables.bottomsheet.playlist

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.soulsearching.composables.dialog.DeletePlaylistDialog
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheetEvents(
    playlistState: PlaylistState,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    navigateToModifyPlaylist : (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val playlistModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    BackHandler(playlistModalSheetState.isVisible) {
        coroutineScope.launch { playlistModalSheetState.hide() }
    }

    if (playlistState.isDeleteDialogShown) {
        DeletePlaylistDialog(
            onPlaylistEvent = onPlaylistEvent,
            confirmAction = {
                coroutineScope.launch { playlistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!playlistModalSheetState.isVisible) {
                            onPlaylistEvent(
                                PlaylistEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                        }
                    }
            }
        )
    }

    if (playlistState.isBottomSheetShown) {
        PlaylistBottomSheet(
            onPlaylistEvent = onPlaylistEvent,
            playlistModalSheetState = playlistModalSheetState,
            navigateToModifyPlaylist = navigateToModifyPlaylist,
            playlistState = playlistState
        )
    }
}
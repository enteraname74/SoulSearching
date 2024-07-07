package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.enteraname74.soulsearching.composables.dialog.DeletePlaylistDialog
import com.github.enteraname74.soulsearching.domain.events.PlaylistEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.PlaylistState
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

    SoulSearchingBackHandler(playlistModalSheetState.isVisible) {
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
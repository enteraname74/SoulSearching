package com.github.soulsearching.composables.bottomsheet.playlist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheet(
    playlistState: PlaylistState,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    playlistModalSheetState: SheetState,
    navigateToModifyPlaylist: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            onPlaylistEvent(
                PlaylistEvent.BottomSheet(
                    isShown = false
                )
            )
        },
        sheetState = playlistModalSheetState,
        dragHandle = {}
    ) {
        PlaylistBottomSheetMenu(
            isFavoritePlaylist = playlistState.selectedPlaylist.isFavorite,
            modifyAction = {
                coroutineScope.launch { playlistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!playlistModalSheetState.isVisible) {
                            onPlaylistEvent(
                                PlaylistEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                            navigateToModifyPlaylist(playlistState.selectedPlaylist.playlistId.toString())
                        }
                    }
            },
            deleteAction = {
                onPlaylistEvent(PlaylistEvent.DeleteDialog(isShown = true))
            },
            quickAccessAction = {
                onPlaylistEvent(PlaylistEvent.UpdateQuickAccessState)
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
            },
            isInQuickAccess = playlistState.selectedPlaylist.isInQuickAccess
        )
    }
}
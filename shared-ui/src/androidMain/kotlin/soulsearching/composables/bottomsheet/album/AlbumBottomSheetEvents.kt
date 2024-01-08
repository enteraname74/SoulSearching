package com.github.soulsearching.composables.bottomsheet.album

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.soulsearching.composables.dialog.DeleteAlbumDialog
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumBottomSheetEvents(
    albumState: AlbumState,
    onAlbumEvent: (AlbumEvent) -> Unit,
    navigateToModifyAlbum: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val albumModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    BackHandler(albumModalSheetState.isVisible) {
        coroutineScope.launch { albumModalSheetState.hide() }
    }

    if (albumState.isDeleteDialogShown) {
        DeleteAlbumDialog(
            onAlbumEvent = onAlbumEvent,
            confirmAction = {
                coroutineScope.launch { albumModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!albumModalSheetState.isVisible) {
                            onAlbumEvent(
                                AlbumEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                        }
                    }
            }
        )
    }

    if (albumState.isBottomSheetShown) {
        AlbumBottomSheet(
            onAlbumEvent = onAlbumEvent,
            albumModalSheetState = albumModalSheetState,
            navigateToModifyAlbum = navigateToModifyAlbum,
            albumState = albumState
        )
    }
}
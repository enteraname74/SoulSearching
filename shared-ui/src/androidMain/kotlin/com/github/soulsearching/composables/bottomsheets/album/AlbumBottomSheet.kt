package com.github.soulsearching.composables.bottomsheets.album

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AlbumBottomSheet(
    albumState: AlbumState,
    onAlbumEvent: (AlbumEvent) -> Unit,
    albumModalSheetState: SheetState,
    navigateToModifyAlbum: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            onAlbumEvent(
                AlbumEvent.BottomSheet(
                    isShown = false
                )
            )
        },
        sheetState = albumModalSheetState,
        dragHandle = {}
    ) {
        AlbumBottomSheetMenu(
            modifyAction = {
                coroutineScope.launch { albumModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!albumModalSheetState.isVisible) {
                            onAlbumEvent(
                                AlbumEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                            navigateToModifyAlbum(albumState.selectedAlbumWithArtist.album.albumId.toString())
                        }
                    }
            },
            deleteAction = {
                onAlbumEvent(AlbumEvent.DeleteDialog(isShown = true))
            },
            quickAccessAction = {
                onAlbumEvent(AlbumEvent.UpdateQuickAccessState)
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
            },
            isInQuickAccess = albumState.selectedAlbumWithArtist.album.isInQuickAccess
        )
    }
}
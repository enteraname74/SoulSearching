package com.github.soulsearching.composables.bottomsheets.album

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.enteraname74.domain.model.Album
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumBottomSheet(
    selectedAlbum: Album,
    onDismiss: () -> Unit,
    onShowDeleteAlbumDialog: () -> Unit,
    onToggleQuickAccessState: () -> Unit,
    albumModalSheetState: SheetState,
    navigateToModifyAlbum: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = albumModalSheetState,
        dragHandle = {}
    ) {
        AlbumBottomSheetMenu(
            modifyAction = {
                coroutineScope.launch { albumModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!albumModalSheetState.isVisible) {
                            onDismiss()
                            navigateToModifyAlbum(selectedAlbum.albumId.toString())
                        }
                    }
            },
            deleteAction = onShowDeleteAlbumDialog,
            quickAccessAction = {
                onToggleQuickAccessState()
                coroutineScope.launch { albumModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!albumModalSheetState.isVisible) {
                            onDismiss()
                        }
                    }
            },
            isInQuickAccess = selectedAlbum.isInQuickAccess
        )
    }
}
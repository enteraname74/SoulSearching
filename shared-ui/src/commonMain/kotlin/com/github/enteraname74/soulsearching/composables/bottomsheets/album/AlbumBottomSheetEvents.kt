package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.soulsearching.composables.dialog.DeleteAlbumDialog
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumBottomSheetEvents(
    selectedAlbum: Album,
    navigateToModifyAlbum: (String) -> Unit,
    isDeleteAlbumDialogShown: Boolean,
    isBottomSheetShown: Boolean,
    onDismissBottomSheet: () -> Unit,
    onSetDeleteAlbumDialogVisibility: (Boolean) -> Unit,
    onToggleQuickAccessState: () -> Unit,
    onDeleteAlbum: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val albumModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    SoulBackHandler(albumModalSheetState.isVisible) {
        coroutineScope.launch { albumModalSheetState.hide() }
    }

    if (isDeleteAlbumDialogShown) {
        DeleteAlbumDialog(
            onDeleteAlbum = {
                onDeleteAlbum()
                onSetDeleteAlbumDialogVisibility(false)
                coroutineScope.launch { albumModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!albumModalSheetState.isVisible) {
                            onDismissBottomSheet()
                        }
                    }
            },
            onDismiss = { onSetDeleteAlbumDialogVisibility(false) }
        )
    }

    if (isBottomSheetShown) {
        AlbumBottomSheet(
            albumModalSheetState = albumModalSheetState,
            navigateToModifyAlbum = navigateToModifyAlbum,
            selectedAlbum = selectedAlbum,
            onDismiss = onDismissBottomSheet,
            onShowDeleteAlbumDialog = { onSetDeleteAlbumDialogVisibility(true) },
            onToggleQuickAccessState = onToggleQuickAccessState
        )
    }
}
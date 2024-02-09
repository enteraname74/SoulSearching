package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import com.github.soulsearching.composables.ModifyAlbumComposable
import com.github.soulsearching.viewmodel.ModifyAlbumViewModel

@Composable
actual fun ModifyAlbumScreen(
    modifyAlbumViewModel: ModifyAlbumViewModel,
    selectedAlbumId: String,
    finishAction: () -> Unit
) {
    ModifyAlbumComposable(
        modifyAlbumViewModel = modifyAlbumViewModel,
        selectedAlbumId = selectedAlbumId,
        finishAction = finishAction,
        selectImage = {}
    )
}
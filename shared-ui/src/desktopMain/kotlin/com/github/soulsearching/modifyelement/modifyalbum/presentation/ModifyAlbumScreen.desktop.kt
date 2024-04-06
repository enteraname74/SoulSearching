package com.github.soulsearching.modifyelement.modifyalbum.presentation

import androidx.compose.runtime.Composable
import com.github.soulsearching.modifyelement.modifyalbum.presentation.composable.ModifyAlbumComposable
import com.github.soulsearching.domain.viewmodel.ModifyAlbumViewModel

@Composable
actual fun ModifyAlbumScreenView(
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
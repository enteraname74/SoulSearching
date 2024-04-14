package com.github.soulsearching.modifyelement.modifyalbum.presentation

import androidx.compose.runtime.Composable
import com.github.soulsearching.modifyelement.modifyalbum.presentation.composable.ModifyAlbumComposable
import com.github.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.settings.domain.ViewSettingsManager

@Composable
actual fun ModifyAlbumScreenView(
    modifyAlbumViewModel: ModifyAlbumViewModel,
    selectedAlbumId: String,
    finishAction: () -> Unit,
    viewSettingsManager: ViewSettingsManager
) {
    ModifyAlbumComposable(
        modifyAlbumViewModel = modifyAlbumViewModel,
        selectedAlbumId = selectedAlbumId,
        finishAction = finishAction,
        selectImage = {}
    )
}
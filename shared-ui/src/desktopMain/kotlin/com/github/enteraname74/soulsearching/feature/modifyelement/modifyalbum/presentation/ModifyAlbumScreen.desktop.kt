package com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain.ModifyAlbumEvent
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.composable.ModifyAlbumComposable
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager

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
        onModifyAlbum = {
            modifyAlbumViewModel.handler.onEvent(ModifyAlbumEvent.UpdateAlbum)
            finishAction()
        },
        selectImage = {},
        onCancel = finishAction
    )
}
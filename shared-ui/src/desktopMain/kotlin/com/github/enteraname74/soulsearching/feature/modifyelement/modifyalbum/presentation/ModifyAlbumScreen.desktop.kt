package com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain.ModifyAlbumEvent
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.composable.ModifyAlbumComposable

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
            modifyAlbumViewModel.onEvent(ModifyAlbumEvent.UpdateAlbum)
            finishAction()
        },
        selectImage = {},
        onCancel = finishAction
    )
}
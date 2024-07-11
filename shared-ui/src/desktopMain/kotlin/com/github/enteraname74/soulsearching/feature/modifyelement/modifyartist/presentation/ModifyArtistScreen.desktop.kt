package com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistEvent
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation.composable.ModifyArtistComposable

@Composable
actual fun ModifyArtistScreenView(
    modifyArtistViewModel: ModifyArtistViewModel,
    selectedArtistId: String,
    finishAction: () -> Unit,
    viewSettingsManager: ViewSettingsManager
) {
    ModifyArtistComposable(
        modifyArtistViewModel = modifyArtistViewModel,
        selectedArtistId = selectedArtistId,
        onModifyArtist = {
            modifyArtistViewModel.onEvent(ModifyArtistEvent.UpdateArtist)
            finishAction()
        },
        selectImage = {},
        onCancel = finishAction
    )
}
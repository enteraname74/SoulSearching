package com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation.composable.ModifyArtistComposable
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistEvent
import com.github.enteraname74.soulsearching.feature.settings.domain.ViewSettingsManager

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
            modifyArtistViewModel.handler.onEvent(ModifyArtistEvent.UpdateArtist)
            finishAction()
        },
        selectImage = {},
        onCancel = finishAction
    )
}
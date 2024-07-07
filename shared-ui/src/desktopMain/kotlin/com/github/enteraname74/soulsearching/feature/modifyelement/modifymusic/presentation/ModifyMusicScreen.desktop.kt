package com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.composable.ModifyMusicComposable
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain.ModifyMusicEvent
import com.github.enteraname74.soulsearching.feature.settings.domain.ViewSettingsManager

@Composable
actual fun ModifyMusicScreenView(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    finishAction: () -> Unit,
    viewSettingsManager: ViewSettingsManager
) {
    ModifyMusicComposable(
        modifyMusicViewModel = modifyMusicViewModel,
        selectedMusicId = selectedMusicId,
        onModifyMusic = {
            modifyMusicViewModel.handler.onEvent(ModifyMusicEvent.UpdateMusic)
            finishAction()
        },
        selectImage = {},
        onCancel = finishAction
    )
}
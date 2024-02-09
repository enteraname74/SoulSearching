package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import com.github.soulsearching.composables.ModifyMusicComposable
import com.github.soulsearching.viewmodel.ModifyMusicViewModel

@Composable
actual fun ModifyMusicScreen(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    finishAction: () -> Unit
) {
    ModifyMusicComposable(
        modifyMusicViewModel = modifyMusicViewModel,
        selectedMusicId = selectedMusicId,
        finishAction = finishAction,
        selectImage = {}
    )
}
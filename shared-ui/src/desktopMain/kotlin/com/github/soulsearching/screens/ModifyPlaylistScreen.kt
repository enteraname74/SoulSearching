package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import com.github.soulsearching.composables.ModifyPlaylistComposable
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModel

@Composable
actual fun ModifyPlaylistScreen(
    modifyPlaylistViewModel: ModifyPlaylistViewModel,
    selectedPlaylistId: String,
    finishAction: () -> Unit
) {
    ModifyPlaylistComposable(
        modifyPlaylistViewModel = modifyPlaylistViewModel,
        selectedPlaylistId = selectedPlaylistId,
        finishAction = finishAction,
        selectImage = {}
    )
}
package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import com.github.soulsearching.composables.ModifyArtistComposable
import com.github.soulsearching.viewmodel.ModifyArtistViewModel

@Composable
actual fun ModifyArtistScreenView(
    modifyArtistViewModel: ModifyArtistViewModel,
    selectedArtistId: String,
    finishAction: () -> Unit,
) {
    ModifyArtistComposable(
        modifyArtistViewModel = modifyArtistViewModel,
        selectedArtistId = selectedArtistId,
        finishAction = finishAction,
        selectImage = {}
    )
}
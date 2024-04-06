package com.github.soulsearching.modifyelement.modifyartist.presentation

import androidx.compose.runtime.Composable
import com.github.soulsearching.modifyelement.modifyartist.presentation.composable.ModifyArtistComposable
import com.github.soulsearching.domain.viewmodel.ModifyArtistViewModel

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
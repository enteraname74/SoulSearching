package com.github.soulsearching.modifyelement.modifyplaylist.presentation

import androidx.compose.runtime.Composable
import com.github.soulsearching.modifyelement.modifyplaylist.presentation.composable.ModifyPlaylistComposable
import com.github.soulsearching.domain.viewmodel.ModifyPlaylistViewModel

@Composable
actual fun ModifyPlaylistScreenView(
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
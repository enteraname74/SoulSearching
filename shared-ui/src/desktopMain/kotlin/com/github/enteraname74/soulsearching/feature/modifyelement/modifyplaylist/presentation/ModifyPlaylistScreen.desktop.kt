package com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation.composable.ModifyPlaylistComposable
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyPlaylistViewModel

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
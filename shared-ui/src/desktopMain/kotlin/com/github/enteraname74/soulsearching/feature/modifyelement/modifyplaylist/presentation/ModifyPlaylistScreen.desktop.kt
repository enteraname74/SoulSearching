package com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation.composable.ModifyPlaylistComposable

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
package com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModel

/**
 * Represent the view for the modify playlist screen.
 */
data class ModifyPlaylistScreen(private val selectedPlaylistId: String): Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ModifyPlaylistViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        ModifyPlaylistScreenView(
            modifyPlaylistViewModel = screenModel,
            selectedPlaylistId = selectedPlaylistId,
            finishAction = {
                navigator.pop()
            }
        )
    }
}

@Composable
expect fun ModifyPlaylistScreenView(
    modifyPlaylistViewModel: ModifyPlaylistViewModel,
    selectedPlaylistId: String,
    finishAction: () -> Unit
)
package com.github.soulsearching.modifyelement.modifymusic.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel

/**
 * Represent the view of the modify music screen.
 */
data class ModifyMusicScreen(private val selectedMusicId: String): Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ModifyMusicViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        ModifyMusicScreenView(
            modifyMusicViewModel = screenModel,
            selectedMusicId = selectedMusicId,
            finishAction = {
                navigator.pop()
            }
        )
    }
}

@Composable
expect fun ModifyMusicScreenView(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    finishAction: () -> Unit
)
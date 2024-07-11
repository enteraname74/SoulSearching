package com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain.ModifyMusicViewModel

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
    finishAction: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
)
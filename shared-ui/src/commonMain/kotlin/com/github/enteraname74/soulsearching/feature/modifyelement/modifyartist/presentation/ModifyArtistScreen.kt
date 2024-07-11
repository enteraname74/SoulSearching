package com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistViewModel

data class ModifyArtistScreen(private val selectedArtistId: String): Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ModifyArtistViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        ModifyArtistScreenView(
            modifyArtistViewModel = screenModel,
            selectedArtistId = selectedArtistId,
            finishAction = {
                navigator.pop()
            }
        )
    }
}

@Composable
expect fun ModifyArtistScreenView(
    modifyArtistViewModel: ModifyArtistViewModel,
    selectedArtistId: String,
    finishAction: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
)
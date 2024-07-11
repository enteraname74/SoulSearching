package com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager

data class ModifyAlbumScreen(private val selectedAlbumId: String): Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ModifyAlbumViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        ModifyAlbumScreenView(
            modifyAlbumViewModel = screenModel,
            selectedAlbumId = selectedAlbumId,
            finishAction = {
                navigator.pop()
            }
        )
    }
}

@Composable
expect fun ModifyAlbumScreenView(
    modifyAlbumViewModel: ModifyAlbumViewModel,
    selectedAlbumId: String,
    finishAction: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
)
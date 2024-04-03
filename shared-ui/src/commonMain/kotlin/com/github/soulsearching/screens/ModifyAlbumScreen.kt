package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.viewmodel.ModifyAlbumViewModel

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
    finishAction: () -> Unit
)
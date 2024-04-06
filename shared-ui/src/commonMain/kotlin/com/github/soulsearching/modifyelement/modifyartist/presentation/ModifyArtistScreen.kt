package com.github.soulsearching.modifyelement.modifyartist.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.domain.viewmodel.ModifyArtistViewModel

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
)
package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class SettingsArtistCoverMethodScreen: Screen {

    @Composable
    override fun Content() {
        val screenModel: SettingsArtistCoverMethodViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow

    }
}
package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverState
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverUi
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.CoverFolderRetrieverScreen

class SettingsArtistCoverMethodScreen: Screen, SettingPage {

    @Composable
    override fun Content() {
        val screenModel: SettingsArtistCoverMethodViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        val state: CoverFolderRetrieverState by screenModel.state.collectAsState()

        CoverFolderRetrieverScreen(
            navigateBack = navigator::pop,
            ui = CoverFolderRetrieverUi(
                title = strings.artistCoverMethodTitle,
                activateText = strings.activateArtistCoverMethod,
                dynamicNameTitle = strings.artistCoverMethodDynamicNameTitle,
                whiteSpaceReplacementTextField = screenModel.whiteSpaceReplacementTextField,
                coverFileNameTextField = screenModel.coverFileNameTextField,
            ),
            state = state,
            actions = screenModel,
        )
    }
}
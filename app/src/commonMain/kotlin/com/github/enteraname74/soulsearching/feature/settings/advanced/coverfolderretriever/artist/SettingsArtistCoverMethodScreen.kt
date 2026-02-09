package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverState
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverUi
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.CoverFolderRetrieverScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsArtistCoverMethodRoute(
    navigateBack: () -> Unit,
) {
    val viewModel: SettingsArtistCoverMethodViewModel = koinViewModel()

    val state: CoverFolderRetrieverState by viewModel.state.collectAsState()

    CoverFolderRetrieverScreen(
        navigateBack = navigateBack,
        ui = CoverFolderRetrieverUi(
            title = strings.artistCoverMethodTitle,
            activateText = strings.activateArtistCoverMethod,
            dynamicNameTitle = strings.artistCoverMethodDynamicNameTitle,
            whiteSpaceReplacementTextField = viewModel.whiteSpaceReplacementTextField,
            coverFileNameTextField = viewModel.coverFileNameTextField,
            extensionTextField = viewModel.extensionTextField,
        ),
        state = state,
        actions = viewModel,
    )
}
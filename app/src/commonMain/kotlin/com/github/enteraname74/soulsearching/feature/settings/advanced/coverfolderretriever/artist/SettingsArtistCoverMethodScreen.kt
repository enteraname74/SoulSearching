package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverState
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverUi
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.CoverFolderRetrieverScreen
import org.koin.compose.viewmodel.koinViewModel
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsArtistCoverMethodRoute(
    navigateBack: () -> Unit,
) {
    val viewModel: SettingsArtistCoverMethodViewModel = koinViewModel()

    val state: CoverFolderRetrieverState by viewModel.state.collectAsState()

    CoverFolderRetrieverScreen(
        navigateBack = navigateBack,
        ui = CoverFolderRetrieverUi(
            title = CoreRes.string.artist_cover_method_title.toTextSpec(),
            activateText = CoreRes.string.activate_artist_cover_method.toTextSpec(),
            dynamicNameTitle = CoreRes.string.artist_cover_method_dynamic_name_title.toTextSpec(),
            whiteSpaceReplacementTextField = viewModel.whiteSpaceReplacementTextField,
            coverFileNameTextField = viewModel.coverFileNameTextField,
            extensionTextField = viewModel.extensionTextField,
        ),
        state = state,
        actions = viewModel,
    )
}

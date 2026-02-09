package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.WriteFilesCheck
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistState

@Composable
fun ModifyArtistRoute(
    viewModel: ModifyArtistViewModel,
    onNavigationState: (ModifyArtistNavigationState) -> Unit,
) {
    val state: ModifyArtistState by viewModel.state.collectAsState()
    val formState: ModifyArtistFormState by viewModel.formState.collectAsState()
    val navigationState: ModifyArtistNavigationState by viewModel.navigationState.collectAsState()
    val bottomSheetState: SoulBottomSheet? by viewModel.bottomSheetState.collectAsState()

    bottomSheetState?.BottomSheet()

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    ModifyArtistScreenView(
        state = state,
        formState = formState,
        navigateBack = viewModel::navigateBack,
        onSelectCover = viewModel::showCoversBottomSheet,
        onValidateModification = viewModel::updateArtist,
    )
}

@Composable
private fun ModifyArtistScreenView(
    state: ModifyArtistState,
    formState: ModifyArtistFormState,
    navigateBack: () -> Unit,
    onSelectCover: () -> Unit,
    onValidateModification: () -> Unit,
) {
    when {
        state is ModifyArtistState.Data && formState is ModifyArtistFormState.Data -> {
            WriteFilesCheck(
                musicsToSave = state.initialArtist.musics,
                onSave = onValidateModification,
            ) { onSave ->
                EditableElementView(
                    title = strings.artistInformation,
                    coverSectionTitle = strings.artistCover,
                    editableElement = state.editableElement,
                    navigateBack = navigateBack,
                    onSelectCover = onSelectCover,
                    onValidateModification = onSave,
                    textFields = formState.textFields,
                )
            }
        }

        else -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
    }
}
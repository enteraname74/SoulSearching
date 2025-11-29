package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistState

@Composable
fun ModifyPlaylistRoute(
    viewModel: ModifyPlaylistViewModel,
    onNavigationState: (ModifyPlaylistNavigationState) -> Unit,
) {
    val state: ModifyPlaylistState by viewModel.state.collectAsState()
    val formState: ModifyPlaylistFormState by viewModel.formState.collectAsState()
    val navigationState: ModifyPlaylistNavigationState by viewModel.navigationState.collectAsState()

    val bottomSheetState: SoulBottomSheet? by viewModel.bottomSheetState.collectAsState()
    bottomSheetState?.BottomSheet()

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    ModifyPlaylistScreenView(
        state = state,
        formState = formState,
        navigateBack = viewModel::navigateBack,
        onSelectCover = viewModel::showCoversBottomSheet,
        onValidateModification = viewModel::updatePlaylist,
    )
}

@Composable
private fun ModifyPlaylistScreenView(
    state: ModifyPlaylistState,
    formState: ModifyPlaylistFormState,
    navigateBack: () -> Unit,
    onSelectCover: () -> Unit,
    onValidateModification: () -> Unit,
) {
    when {
        state is ModifyPlaylistState.Data && formState is ModifyPlaylistFormState.Data -> {
            EditableElementView(
                title = strings.playlistInformation,
                coverSectionTitle = strings.playlistCover,
                editableElement = state.editableElement,
                navigateBack = navigateBack,
                onSelectCover = onSelectCover,
                onValidateModification = onValidateModification,
                textFields = formState.textFields,
            )
        }

        state is ModifyPlaylistState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
    }
}
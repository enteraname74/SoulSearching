package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.WriteFilesCheck
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumState

@Composable
fun ModifyAlbumRoute(
    viewModel: ModifyAlbumViewModel,
    onNavigationState: (ModifyAlbumNavigationState) -> Unit,
) {
    val state: ModifyAlbumState by viewModel.state.collectAsState()
    val formState: ModifyAlbumFormState by viewModel.formState.collectAsState()
    val navigationState: ModifyAlbumNavigationState by viewModel.navigationState.collectAsState()

    val bottomSheetState: SoulBottomSheet? by viewModel.bottomSheetState.collectAsState()
    bottomSheetState?.BottomSheet()

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    ModifyAlbumScreenView(
        state = state,
        formState = formState,
        navigateBack = viewModel::navigateBack,
        onSelectCover = viewModel::showCoversBottomSheet,
        onValidateModification = viewModel::updateAlbum,
    )
}

@Composable
private fun ModifyAlbumScreenView(
    state: ModifyAlbumState,
    formState: ModifyAlbumFormState,
    navigateBack: () -> Unit,
    onSelectCover: () -> Unit,
    onValidateModification: () -> Unit,
) {
    when {
        state is ModifyAlbumState.Data && formState is ModifyAlbumFormState.Data -> {

            WriteFilesCheck(
                musicsToSave = state.initialAlbum.musics,
                onSave = onValidateModification,
            ) { onSave ->
                EditableElementView(
                    title = strings.albumInformation,
                    coverSectionTitle = strings.albumCover,
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
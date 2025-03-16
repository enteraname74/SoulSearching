package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.feature.editableelement.WriteFilesCheck
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementScreen
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistState
import java.util.*

data class ModifyArtistScreen(
    private val selectedArtistId: String
) : EditableElementScreen(selectedArtistId) {
    private val artistId: UUID = UUID.fromString(selectedArtistId)

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ModifyArtistViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: ModifyArtistState by screenModel.state.collectAsState()
        val formState: ModifyArtistFormState by screenModel.formState.collectAsState()
        val navigationState: ModifyArtistNavigationState by screenModel.navigationState.collectAsState()
        val bottomSheetState: SoulBottomSheet? by screenModel.bottomSheetState.collectAsState()

        bottomSheetState?.BottomSheet()

        LaunchInit {
            screenModel.init(artistId = artistId)
        }

        LaunchedEffect(navigationState) {
            when (navigationState) {
                ModifyArtistNavigationState.Back -> {
                    navigator.pop()
                    screenModel.consumeNavigation()
                }

                ModifyArtistNavigationState.Idle -> {
                    /*no-op*/
                }
            }
        }

        ModifyArtistScreenView(
            state = state,
            formState = formState,
            navigateBack = { navigator.pop() },
            onSelectCover = screenModel::showCoversBottomSheet,
            onValidateModification = screenModel::updateArtist,
        )
    }
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
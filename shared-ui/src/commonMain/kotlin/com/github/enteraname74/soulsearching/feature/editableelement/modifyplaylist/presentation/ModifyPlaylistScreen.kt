package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementScreen
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistState
import io.github.vinceglb.filekit.core.PlatformFile
import java.util.*

/**
 * Represent the view for the modify playlist screen.
 */
data class ModifyPlaylistScreen(
    private val selectedPlaylistId: String
) : EditableElementScreen(selectedPlaylistId) {
    private val playlistId: UUID = UUID.fromString(selectedPlaylistId)

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ModifyPlaylistViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: ModifyPlaylistState by screenModel.state.collectAsState()
        val formState: ModifyPlaylistFormState by screenModel.formState.collectAsState()
        val navigationState: ModifyPlaylistNavigationState by screenModel.navigationState.collectAsState()

        LaunchedEffect(navigationState) {
            when (navigationState) {
                ModifyPlaylistNavigationState.Back -> {
                    navigator.pop()
                    screenModel.consumeNavigation()
                }

                ModifyPlaylistNavigationState.Idle -> {
                    /*no-op*/
                }
            }
        }

        var isPlaylistFetched by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(isPlaylistFetched) {
            if (!isPlaylistFetched) {
                screenModel.init(playlistId = playlistId)
                isPlaylistFetched = true
            }
        }

        ModifyPlaylistScreenView(
            state = state,
            formState = formState,
            navigateBack = { navigator.pop() },
            onNewImageSet = screenModel::setNewCover,
            onValidateModification = screenModel::updatePlaylist,
        )
    }
}

@Composable
private fun ModifyPlaylistScreenView(
    state: ModifyPlaylistState,
    formState: ModifyPlaylistFormState,
    navigateBack: () -> Unit,
    onNewImageSet: (cover: PlatformFile) -> Unit,
    onValidateModification: () -> Unit,
) {
    when {
        state is ModifyPlaylistState.Data && formState is ModifyPlaylistFormState.Data -> {
            EditableElementView(
                title = strings.playlistInformation,
                coverSectionTitle = strings.playlistCover,
                editableElement = state.editableElement,
                navigateBack = navigateBack,
                onNewImageSet = onNewImageSet,
                onValidateModification = onValidateModification,
                textFields = formState.textFields,
                textFieldsLabels = listOf(
                    strings.playlistName
                )
            )
        }

        state is ModifyPlaylistState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
    }
}
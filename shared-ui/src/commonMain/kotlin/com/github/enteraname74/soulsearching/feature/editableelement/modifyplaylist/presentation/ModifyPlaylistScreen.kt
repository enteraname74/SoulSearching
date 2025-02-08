package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementScreen
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistState
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
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
        val screenModel = koinScreenModel<ModifyPlaylistViewModel>()
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

        LaunchInit {
            screenModel.init(playlistId = playlistId)
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
    val imagePickerLauncher = rememberFilePickerLauncher(
        type = PickerType.Image,
    ) { file ->
        if (file == null) return@rememberFilePickerLauncher
        onNewImageSet(file)
    }

    when {
        state is ModifyPlaylistState.Data && formState is ModifyPlaylistFormState.Data -> {
            EditableElementView(
                title = strings.playlistInformation,
                coverSectionTitle = strings.playlistCover,
                editableElement = state.editableElement,
                navigateBack = navigateBack,
                onSelectCover = { imagePickerLauncher.launch() },
                onValidateModification = onValidateModification,
                textFields = formState.textFields,
            )
        }

        state is ModifyPlaylistState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
    }
}
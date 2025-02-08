package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation

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
import com.github.enteraname74.soulsearching.feature.editableelement.WriteFilesCheck
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementScreen
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumState
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import java.util.*

data class ModifyAlbumScreen(
    private val selectedAlbumId: String
) : EditableElementScreen(selectedAlbumId) {
    private val albumId: UUID = UUID.fromString(selectedAlbumId)

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ModifyAlbumViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: ModifyAlbumState by screenModel.state.collectAsState()
        val formState: ModifyAlbumFormState by screenModel.formState.collectAsState()
        val navigationState: ModifyAlbumNavigationState by screenModel.navigationState.collectAsState()

        LaunchInit {
            screenModel.init(albumId = albumId)
        }

        LaunchedEffect(navigationState) {
            when (navigationState) {
                ModifyAlbumNavigationState.Back -> {
                    navigator.pop()
                    screenModel.consumeNavigation()
                }

                ModifyAlbumNavigationState.Idle -> {
                    /*no-op*/
                }
            }
        }

        ModifyAlbumScreenView(
            state = state,
            formState = formState,
            navigateBack = { navigator.pop() },
            onNewImageSet = screenModel::setNewCover,
            onValidateModification = screenModel::updateAlbum,
        )
    }
}

@Composable
private fun ModifyAlbumScreenView(
    state: ModifyAlbumState,
    formState: ModifyAlbumFormState,
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
                    onSelectCover = { imagePickerLauncher.launch() },
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
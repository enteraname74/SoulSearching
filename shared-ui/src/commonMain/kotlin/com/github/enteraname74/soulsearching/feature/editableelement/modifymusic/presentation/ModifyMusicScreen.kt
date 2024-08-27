package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementScreen
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicState
import com.github.enteraname74.soulsearching.feature.editableelement.WriteFilesCheck
import io.github.vinceglb.filekit.core.PlatformFile
import java.util.*

/**
 * Represent the view of the modify music screen.
 */
data class ModifyMusicScreen(
    private val selectedMusicId: String
): EditableElementScreen(selectedMusicId) {
    private val musicId: UUID = UUID.fromString(selectedMusicId)

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ModifyMusicViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: ModifyMusicState by screenModel.state.collectAsState()
        val formState: ModifyMusicFormState by screenModel.formState.collectAsState()
        val navigationState: ModifyMusicNavigationState by screenModel.navigationState.collectAsState()

        var isSelectedMusicFetched by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(isSelectedMusicFetched) {
            if (!isSelectedMusicFetched) {
                screenModel.init(musicId = musicId)
                isSelectedMusicFetched = true
            }
        }

        LaunchedEffect(navigationState) {
            when (navigationState) {
                ModifyMusicNavigationState.Back -> {
                    navigator.pop()
                    screenModel.consumeNavigation()
                }
                ModifyMusicNavigationState.Idle ->  {
                    /*no-op*/
                }
            }
        }

        ModifyMusicScreenView(
            state = state,
            formState = formState,
            navigateBack = { navigator.pop() },
            onNewImageSet = screenModel::setNewCover,
            onValidateModification = screenModel::updateMusic,
        )
    }
}

@Composable
private fun ModifyMusicScreenView(
    state: ModifyMusicState,
    formState: ModifyMusicFormState,
    navigateBack: () -> Unit,
    onNewImageSet: (cover: PlatformFile) -> Unit,
    onValidateModification: () -> Unit,
) {

    when {
        state is ModifyMusicState.Data && formState is ModifyMusicFormState.Data -> {

            WriteFilesCheck(
                musicsToSave = listOf(
                    state.initialMusic
                ),
                onSave = onValidateModification,
            ) { onSave ->
                EditableElementView(
                    title = strings.musicInformation,
                    coverSectionTitle = strings.albumCover,
                    editableElement = state.editableElement,
                    navigateBack = navigateBack,
                    onNewImageSet = onNewImageSet,
                    onValidateModification = onSave,
                    textFields = formState.textFields,
                    textFieldsLabels = listOf(
                        strings.musicName,
                        strings.albumName,
                        strings.artistName,
                    )
                )
            }
        }

        else -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
    }
}
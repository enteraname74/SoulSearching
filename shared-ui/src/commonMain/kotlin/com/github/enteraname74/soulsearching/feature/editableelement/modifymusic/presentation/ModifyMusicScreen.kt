package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.feature.editableelement.WriteFilesCheck
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementAddArtist
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementScreen
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicState
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
        val screenModel = koinScreenModel<ModifyMusicViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: ModifyMusicState by screenModel.state.collectAsState()
        val formState: ModifyMusicFormState by screenModel.formState.collectAsState()
        val navigationState: ModifyMusicNavigationState by screenModel.navigationState.collectAsState()
        val bottomSheetState: SoulBottomSheet? by screenModel.bottomSheetState.collectAsState()

        bottomSheetState?.BottomSheet()

        LaunchInit {
            screenModel.init(musicId = musicId)
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
            onSelectCover = screenModel::showCoverBottomSheet,
            onValidateModification = screenModel::updateMusic,
            addArtistField = screenModel::addArtistField,
        )
    }

    @Composable
    fun MusicPathFooter(
        musicPath: String,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = UiConstants.Spacing.medium,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.verySmall)
        ) {
            Text(
                text = strings.musicPath,
                style = UiConstants.Typography.bodyVerySmall,
                color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
            )
            Text(
                text = musicPath,
                style = UiConstants.Typography.bodyVerySmall,
                color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
            )
        }
    }

    @Composable
    private fun ModifyMusicScreenView(
        state: ModifyMusicState,
        formState: ModifyMusicFormState,
        navigateBack: () -> Unit,
        onSelectCover: () -> Unit,
        onValidateModification: () -> Unit,
        addArtistField: () -> Unit,
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
                        onSelectCover = onSelectCover,
                        onValidateModification = onSave,
                        textFields = formState.textFields,
                        extraFormBottomContent = {
                            EditableElementAddArtist(
                                onClick = addArtistField,
                            )
                        },
                        extraFormTopContent = {
                            MusicPathFooter(
                                musicPath = state.initialMusic.path,
                            )
                        }
                    )
                }
            }

            else -> SoulLoadingScreen(
                navigateBack = navigateBack,
            )
        }
    }
}
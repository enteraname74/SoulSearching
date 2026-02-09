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
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.WriteFilesCheck
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementAddArtist
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicState

@Composable
fun ModifyMusicRoute(
    viewModel: ModifyMusicViewModel,
    onNavigationState: (ModifyMusicNavigationState) -> Unit,
) {
    val state: ModifyMusicState by viewModel.state.collectAsState()
    val formState: ModifyMusicFormState by viewModel.formState.collectAsState()
    val navigationState: ModifyMusicNavigationState by viewModel.navigationState.collectAsState()
    val bottomSheetState: SoulBottomSheet? by viewModel.bottomSheetState.collectAsState()

    bottomSheetState?.BottomSheet()

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    ModifyMusicScreenView(
        state = state,
        formState = formState,
        navigateBack = viewModel::navigateBack,
        onSelectCover = viewModel::showCoverBottomSheet,
        onValidateModification = viewModel::updateMusic,
        addArtistField = viewModel::addArtistField,
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
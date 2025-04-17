package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverState
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverUi
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.mode.CoverFolderRetrieverFileMode
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.mode.CoverFolderRetrieverFolderMode
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun CoverFolderRetrieverScreen(
    navigateBack: () -> Unit,
    ui: CoverFolderRetrieverUi,
    state: CoverFolderRetrieverState,
    actions: CoverFolderRetrieverActions,
) {
    val focusManager = LocalFocusManager.current
    SettingPage(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        navigateBack = navigateBack,
        title = ui.title,
    ) {
        item {
            SoulMenuSwitch(
                title = ui.activateText,
                isChecked = state.coverFolderRetriever.isActivated,
                toggleAction = actions::onToggleActivation,
            )
        }
        item {
            AnimatedVisibility(
                modifier = Modifier
                    .padding(horizontal = UiConstants.Spacing.large),
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = state.coverFolderRetriever.isActivated,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
                ) {
                    CoverFolderRetrieverExamplePath(
                        coverFolderRetriever = state.coverFolderRetriever,
                    )
                    CoverFolderRetrieverRules(
                        actions = actions,
                        coverFolderRetriever = state.coverFolderRetriever,
                        title = ui.dynamicNameTitle,
                        whiteSpaceReplacementTextField = ui.whiteSpaceReplacementTextField,
                    )
                    CoverFolderRetrieverFolderMode(
                        actions = actions,
                        coverFolderRetriever = state.coverFolderRetriever,
                        coverFilNameTextField = ui.coverFileNameTextField
                    )
                    CoverFolderRetrieverFileMode(
                        actions = actions,
                        coverFolderRetriever = state.coverFolderRetriever,
                    )
                }
            }
        }
    }
}
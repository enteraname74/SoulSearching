package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.chainIf
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus
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
    val disabledAlpha: Float by animateFloatAsState(
        targetValue = if (state.coverFolderRetriever.isActivated) 1f else ContentAlpha.disabled,
        animationSpec = tween(UiConstants.AnimationDuration.short),
    )
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
            val interactionSource = remember { MutableInteractionSource() }

            Column(
                modifier = Modifier
                    .alpha(disabledAlpha)
                    .chainIf(!state.coverFolderRetriever.isActivated) {
                        Modifier.disableFocus()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {}
                            )
                    },
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
            ) {
                val path = state.coverFolderRetriever.buildDynamicCoverPath(
                    dynamicName = EXAMPLE_ARTIST_NAME,
                )

                CoverFolderRetrieverIncomplete(
                    isComplete = path == null,
                )
                CoverFolderRetrieverExamplePath(
                    path = path,
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

const val EXAMPLE_ARTIST_NAME = "John Doe"
package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus
import com.github.enteraname74.soulsearching.coreui.ext.toDp
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

            var coverSectionHeight by rememberSaveable { mutableIntStateOf(0) }

            Box {
                Column(
                    modifier = Modifier
                        .alpha(disabledAlpha)
                        .onGloballyPositioned { layoutCoordinates ->
                            coverSectionHeight = layoutCoordinates.size.height
                        }
                        .padding(horizontal = UiConstants.Spacing.large),
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
                DisabledHover(
                    modifier = Modifier
                        .height(coverSectionHeight.toDp()),
                    isVisible = !state.coverFolderRetriever.isActivated
                )
            }
        }
    }
}

@Composable
private fun DisabledHover(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = UiConstants.AnimationDuration.short
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = UiConstants.AnimationDuration.short
            )
        ),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .disableFocus()
        )
    }
}

const val EXAMPLE_ARTIST_NAME = "John Doe"
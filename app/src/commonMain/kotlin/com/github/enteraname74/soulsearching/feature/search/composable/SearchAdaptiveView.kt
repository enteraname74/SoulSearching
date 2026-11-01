package com.github.enteraname74.soulsearching.feature.search.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.ext.swipeableView
import com.github.enteraname74.soulsearching.feature.swipeableview.SwipeableViewManager
import com.github.enteraname74.soulsearching.feature.swipeableview.SwipeableViewManagerHandler
import kotlin.math.roundToInt

@Composable
internal fun SearchAdaptiveView(
    swipeableViewManager: SwipeableViewManager,
    focusRequester: FocusRequester,
    dismiss: () -> Unit,
    content: @Composable (onBack: () -> Unit) -> Unit,
) {
    if (rememberWindowSize() == WindowSize.Large) {
        DialogHolder(
            focusRequester = focusRequester,
            dismiss = dismiss,
            content = content,
        )
    } else {
        SwipeableHolder(
            swipeableViewManager = swipeableViewManager,
            focusRequester = focusRequester,
            dismiss = dismiss,
            content = content,
        )
    }
}

@Composable
private fun DialogHolder(
    focusRequester: FocusRequester,
    dismiss: () -> Unit,
    content: @Composable (onBack: () -> Unit) -> Unit,
) {
    LaunchInit { focusRequester.requestFocus() }
    Dialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.6f)
                .fillMaxHeight(.6f)
                .clip(RoundedCornerShape(UiConstants.Spacing.large))
                .background(SoulSearchingColorTheme.colorScheme.surface)
        ) {
            content(dismiss)
        }
    }
}

@Composable
private fun SwipeableHolder(
    swipeableViewManager: SwipeableViewManager,
    focusRequester: FocusRequester,
    dismiss: () -> Unit,
    content: @Composable (onBack: () -> Unit) -> Unit,
) {
    SwipeableViewManagerHandler(swipeableViewManager)

    val previousState by swipeableViewManager.previousState.collectAsState()

    LaunchedEffect(swipeableViewManager.currentValue, previousState) {
        if (
            swipeableViewManager.currentValue == BottomSheetStates.COLLAPSED
            && (previousState != BottomSheetStates.COLLAPSED && previousState != null)) {
            dismiss()
        }
    }

    LaunchInit {
        swipeableViewManager.animateTo(
            newState = BottomSheetStates.EXPANDED,
            onExpanded = {
                focusRequester.requestFocus()
            }
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val maxHeight = with(LocalDensity.current) {
            this@BoxWithConstraints.maxHeight.toPx() + getNavigationBarPadding()
        }
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = swipeableViewManager.offset.roundToInt()
                    )
                }
                .swipeableView(
                    swipeableViewManager = swipeableViewManager,
                    anchors = mapOf(
                        maxHeight to BottomSheetStates.COLLAPSED,
                        0f to BottomSheetStates.EXPANDED,
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoulSearchingColorTheme.colorScheme.background)
            ) {
                content(swipeableViewManager::closeIfPossible)
            }
        }
    }
}
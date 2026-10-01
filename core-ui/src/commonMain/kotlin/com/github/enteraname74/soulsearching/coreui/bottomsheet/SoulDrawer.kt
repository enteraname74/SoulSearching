package com.github.enteraname74.soulsearching.coreui.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.zIndex
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.multiselection.LocalMultiSelectionTopBarHeight
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun SoulDrawer(
    onClose: () -> Unit,
    colors: SoulBottomSheetColors = SoulBottomSheetDefaults.colors(),
    onRemoveRequest: (suspend () -> Unit) -> Unit = {},
    content: @Composable (closeWithAnim: (callback: () -> Unit) -> Unit) -> Unit,
) {
    var isShown: SoulDrawerState by rememberSaveable {
        mutableStateOf(SoulDrawerState.Init)
    }
    val coroutineScope = rememberCoroutineScope()

    val windowWidth = rememberWindowWidth().toInt()

    LaunchedEffect(Unit) {
        isShown = SoulDrawerState.Open
    }

    LaunchedEffect(Unit) {
        onRemoveRequest {
            isShown = SoulDrawerState.Closed
            delay(UiConstants.AnimationDuration.normal.toLong().milliseconds)
        }
    }

    val closeWithAnim: (callback: () -> Unit) -> Unit = { callback ->
        coroutineScope.launch {
            isShown = SoulDrawerState.Closed
            delay(UiConstants.AnimationDuration.normal.toLong().milliseconds)
            onClose()
            callback()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        AnimatedVisibility(
            visible = isShown == SoulDrawerState.Open,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Scrim(onClose = closeWithAnim)
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = isShown == SoulDrawerState.Open,
            enter = slideInHorizontally(
                initialOffsetX = { windowWidth },
                animationSpec = tween(durationMillis = UiConstants.AnimationDuration.normal)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { windowWidth },
                animationSpec = tween(durationMillis = UiConstants.AnimationDuration.normal),
            )
        ) {
            Drawer(
                containerColor = colors.containerColor,
            ) {
                content(closeWithAnim)
            }
        }
    }
}

@Composable
private fun Drawer(
    containerColor: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(.4f)
            .background(color = containerColor)
            .padding(top = LocalMultiSelectionTopBarHeight.current),
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Scrim(
    onClose: (callback: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            // handle pointer input
            .pointerInput(onClose) { detectTapGestures { onClose {} } }
            // handle accessibility services
            .semantics(mergeDescendants = true) {
                contentDescription = ""
                onClick {
                    onClose {}
                    true
                }
            }
            // handle physical keyboard input
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onClose {}
                    true
                } else {
                    false
                }
            }
            // draw scrim
            .background(BottomSheetDefaults.ScrimColor)
    )
}

private enum class SoulDrawerState {
    Open,
    Closed,
    Init
}

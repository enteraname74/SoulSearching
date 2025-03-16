package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.clickableWithHandCursor(onClick: () -> Unit): Modifier =
    this
        .pointerHoverIcon(PointerIcon.Hand)
        .clickable(onClick = onClick)

fun Modifier.clickableIf(enabled: Boolean, block: () -> Unit): Modifier =
    if (enabled) {
        this
            .clickableWithHandCursor {
                block()
            }
    } else {
        this
    }

fun Modifier.optionalClickable(onClick: (() -> Unit)?): Modifier =
    if (onClick == null) {
        this
    } else {
        this.clickableWithHandCursor {
            onClick()
        }
    }

fun Modifier.optionalClickable(onClick: (() -> Unit)?, onLongClick: (() -> Unit)?): Modifier =
    if (onClick == null && onLongClick == null) {
        this
    } else if (onLongClick == null) {
        this.optionalClickable(onClick)
    } else {
        this.combinedClickableWithRightClick(
            onClick = onClick ?: {},
            onLongClick = onLongClick,
        )
    }

fun Modifier.chainIf(
    condition: Boolean,
    modifier: () -> Modifier,
): Modifier {
    val addedModifier = if (condition) modifier() else Modifier
    return this.then(addedModifier)
}

/**
 * Disable the focus and click action on a composable.
 */
fun Modifier.disableFocus(): Modifier = this
    .pointerInput(Unit) { detectTapGestures {  } }
    .semantics(mergeDescendants = true) {
        contentDescription = ""
        onClick { true }
    }
    .onKeyEvent { true }


expect fun Modifier.combinedClickableWithRightClick(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier

@Composable
expect fun Modifier.blurCompat(
    radius: Dp? = null
): Modifier

internal val RECENT_DEFAULT_BLUR = 8.dp
internal const val LEGACY_DEFAULT_BLUR = 52
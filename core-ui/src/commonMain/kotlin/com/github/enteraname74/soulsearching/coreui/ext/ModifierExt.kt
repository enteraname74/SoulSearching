package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

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


expect fun Modifier.combinedClickableWithRightClick(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier
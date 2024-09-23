package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.onClick
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton

fun Modifier.clickableIf(enabled: Boolean, block: () -> Unit): Modifier =
    if (enabled) {
        this.clickable {
            block()
        }
    } else {
        this
    }

fun Modifier.optionalClickable(onClick: (() -> Unit)?): Modifier =
    if (onClick == null) {
        this
    } else {
        this.clickable {
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

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.combinedClickableWithRightClick(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier =
    this
        .onClick(
            matcher = PointerMatcher.mouse(PointerButton.Secondary),
            onClick = onLongClick,
        )
        .onClick(
            matcher = PointerMatcher.mouse(PointerButton.Primary),
            onClick = onClick,
            onLongClick = onLongClick,
        )
package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Modifier

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

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.optionalClickable(onClick: (() -> Unit)?, onLongClick: (() -> Unit)?): Modifier =
    if (onClick == null && onLongClick == null) {
        this
    } else if (onLongClick == null) {
        this.optionalClickable(onClick)
    } else {
        this.combinedClickable(
            onClick = onClick ?: {},
            onLongClick = onLongClick,
        )
    }
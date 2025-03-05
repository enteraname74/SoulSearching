package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.combinedClickableWithRightClick(
    onClick: () -> Unit,
    onLongClick: () -> Unit
): Modifier =
    this
        .pointerHoverIcon(PointerIcon.Hand)
        .onClick(
            matcher = PointerMatcher.mouse(PointerButton.Secondary),
            onClick = onLongClick,
        )
        .onClick(
            matcher = PointerMatcher.mouse(PointerButton.Primary),
            onClick = onClick,
            onLongClick = onLongClick,
        )

@Composable
actual fun Modifier.blurCompat(
    radius: Dp?
): Modifier =
    this.blur(radius = radius ?: RECENT_DEFAULT_BLUR)
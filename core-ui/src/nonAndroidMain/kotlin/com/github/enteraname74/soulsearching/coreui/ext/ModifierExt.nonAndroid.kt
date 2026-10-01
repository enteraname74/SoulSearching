package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp

@Composable
@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.combinedClickableWithRightClick(
    enabled: Boolean,
    withIndication: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
): Modifier {
    val indication = if (withIndication) LocalIndication.current else null

    return this
        .pointerHoverIcon(PointerIcon.Hand)
        // Mouse right-click
        .onClick(
            enabled = enabled,
            matcher = PointerMatcher.mouse(PointerButton.Secondary),
            onClick = onLongClick,
        )
        // Mouse primary-click + touchscreen tap/long-press
        .combinedClickable(
            enabled = enabled,
            interactionSource = null,
            indication = indication,
            onClick = onClick,
            onLongClick = onLongClick,
        )
}

@Composable
actual fun Modifier.blurCompat(
    radius: Dp?
): Modifier =
    this.blur(radius = radius ?: RECENT_DEFAULT_BLUR)

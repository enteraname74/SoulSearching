package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.onClick
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.combinedClickableWithRightClick(
    onClick: () -> Unit,
    onLongClick: () -> Unit
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
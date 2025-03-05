package com.github.enteraname74.soulsearching.coreui.ext

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skydoves.cloudy.cloudy

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.combinedClickableWithRightClick(
    onClick: () -> Unit,
    onLongClick: () -> Unit
): Modifier =
    this
        .pointerHoverIcon(PointerIcon.Hand)
        .combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )

@Composable
actual fun Modifier.blurCompat(
    radius: Dp?
): Modifier =
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
        this.blur(radius ?: RECENT_DEFAULT_BLUR)
    } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R ) {
        this.cloudy(radius = radius?.value?.toInt() ?: LEGACY_DEFAULT_BLUR)
    } else {
        this.blur(radius = radius ?: RECENT_DEFAULT_BLUR)
    }



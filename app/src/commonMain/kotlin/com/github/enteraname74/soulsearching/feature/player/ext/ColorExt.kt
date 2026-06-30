package com.github.enteraname74.soulsearching.feature.player.ext

import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.UiConstants


fun Color.disabledIfNoAction(action: (() -> Unit)?): Color =
    copy(alpha = action?.let { 1f } ?: UiConstants.ALPHA_DISABLED)
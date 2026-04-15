package com.github.enteraname74.soulsearching.feature.player.ext

import androidx.compose.material.ContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun Color.disabledIfNoAction(action: (() -> Unit)?): Color =
    copy(alpha = action?.let { 1f } ?: ContentAlpha.disabled)
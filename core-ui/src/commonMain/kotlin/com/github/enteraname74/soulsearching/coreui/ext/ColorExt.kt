package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.isDark(): Boolean =
    this.luminance() < 0.5
package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.util.PlatformUtils

@Composable
actual fun PlatformUtils.isMouseAndKeyboardOnly(): Boolean = false
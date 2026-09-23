package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.domain.util.PlatformUtils

@Composable
expect fun PlatformUtils.isMouseAndKeyboardOnly(): Boolean
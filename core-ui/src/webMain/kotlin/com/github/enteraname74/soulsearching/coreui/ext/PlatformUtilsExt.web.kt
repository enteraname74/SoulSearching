package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.util.PlatformUtils
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

@Composable
actual fun PlatformUtils.suspectMouseOnly(): Boolean {
    val windowSize = rememberWindowSize()
    return windowSize == WindowSize.Large
}
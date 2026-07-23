package com.github.enteraname74.soulsearching.coreui.loading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

@Composable
internal actual fun FullScreenLoadingModifier() {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window

    LaunchedEffect(window) {
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
        }
    }
}

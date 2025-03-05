package com.github.enteraname74.soulsearching.coreui.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
fun getNavigationBarPadding(): Int = with(LocalDensity.current) {
    WindowInsets.navigationBars.getBottom(this)
}

@Composable
fun getStatusBarPadding(): Int = with(LocalDensity.current) {
    WindowInsets.statusBars.getTop(this)
}
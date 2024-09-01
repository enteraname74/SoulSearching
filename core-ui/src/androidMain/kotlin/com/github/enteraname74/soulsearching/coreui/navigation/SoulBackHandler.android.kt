package com.github.enteraname74.soulsearching.coreui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun SoulBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(
        enabled = enabled,
        onBack = onBack
    )
}
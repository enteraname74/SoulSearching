package com.github.enteraname74.soulsearching.coreui.navigation

import androidx.compose.runtime.Composable

@Composable
expect fun SoulSearchingBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)
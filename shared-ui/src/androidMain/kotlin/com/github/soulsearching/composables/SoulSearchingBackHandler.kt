package com.github.soulsearching.composables

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun SoulSearchingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    BackHandler(
        enabled = enabled,
        onBack = onBack
    )
}
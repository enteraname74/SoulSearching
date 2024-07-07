package com.github.soulsearching.composables

import androidx.compose.runtime.Composable

@Composable
expect fun SoulSearchingBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)
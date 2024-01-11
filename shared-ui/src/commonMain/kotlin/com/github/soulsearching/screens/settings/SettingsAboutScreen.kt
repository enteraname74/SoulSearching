package com.github.soulsearching.screens.settings

import androidx.compose.runtime.Composable

@Composable
expect fun SettingsAboutScreen(
    finishAction: () -> Unit,
    navigateToDevelopers: () -> Unit,
)

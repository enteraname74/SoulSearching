package com.github.enteraname74.soulsearching.feature.settings.aboutpage.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.presentation.composable.SettingsAboutComposable

@Composable
actual fun SettingsAboutScreenView(
    finishAction: () -> Unit,
    navigateToDevelopers: () -> Unit,
) {
    val versionName = "1.0.0"

    SettingsAboutComposable(
        versionName = versionName,
        versionNameAction = {},
        navigateToDevelopers = navigateToDevelopers,
        finishAction = finishAction
    )
}
package com.github.soulsearching.screens.settings

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.BuildConfig
import com.github.soulsearching.composables.settings.SettingsAboutComposable
import com.github.soulsearching.strings

@Composable
actual fun SettingsAboutScreen(
    finishAction: () -> Unit,
    navigateToDevelopers: () -> Unit,
) {
    val context = LocalContext.current
    val versionName = BuildConfig.VERSION_NAME

    SettingsAboutComposable(
        versionName = versionName,
        versionNameAction = {
            Toast.makeText(
                context,
                strings.versionNameActionText,
                Toast.LENGTH_SHORT
            ).show()
        },
        navigateToDevelopers = navigateToDevelopers,
        finishAction = finishAction
    )
}
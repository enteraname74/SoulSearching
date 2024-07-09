package com.github.enteraname74.soulsearching.feature.settings.aboutpage.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.BuildConfig
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.presentation.composable.SettingsAboutComposable
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
actual fun SettingsAboutScreenView(
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
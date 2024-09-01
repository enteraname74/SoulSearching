package com.github.enteraname74.soulsearching.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.domain.Typography
import com.github.enteraname74.soulsearching.theme.isInDarkTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SoulSearchingTheme(
    content: @Composable () -> Unit
) {

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isInDarkTheme()

    DisposableEffect(systemUiController, useDarkIcons) {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
        )
        onDispose {}
    }

    MaterialTheme(
        typography = Typography,
        content = content
    )
}
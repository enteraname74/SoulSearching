package com.github.soulsearching.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.theme.SoulSearchingPalette
import com.github.soulsearching.theme.Typography
import com.github.soulsearching.theme.primaryColorDark
import com.github.soulsearching.theme.primaryColorLight
import com.github.soulsearching.theme.secondaryColorDark
import com.github.soulsearching.theme.secondaryColorLight
import com.github.soulsearching.theme.subTextColorDark
import com.github.soulsearching.theme.subTextColorLight
import com.github.soulsearching.theme.textColorDark
import com.github.soulsearching.theme.textColorLight
import com.github.soulsearching.theme.thirdColorDark
import com.github.soulsearching.theme.thirdColorLight
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = primaryColorDark,
    secondary = secondaryColorDark,
    tertiary = thirdColorDark,
    onPrimary = textColorDark,
    onSecondary = textColorDark,
    onTertiary = textColorDark,
    outline = subTextColorDark
)

private val LightColorScheme = lightColorScheme(
    primary = primaryColorLight,
    secondary = secondaryColorLight,
    tertiary = thirdColorLight,
    onPrimary = textColorLight,
    onSecondary = textColorLight,
    onTertiary = textColorLight,
    outline = subTextColorLight

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SoulSearchingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    DisposableEffect(systemUiController, useDarkIcons) {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = colorScheme.primary,
            darkIcons = useDarkIcons
        )
        onDispose {}
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
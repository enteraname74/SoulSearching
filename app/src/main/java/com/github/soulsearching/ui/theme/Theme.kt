package com.github.soulsearching.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = primaryColorDark,
    secondary = secondaryColorDark,
    tertiary = thirdColorDark,
    onPrimary = textColorDark,
    onSecondary = textColorDark,
    onTertiary = textColorDark,
    outline = highLightedTextDark
)

private val LightColorScheme = lightColorScheme(
    primary = primaryColorLight,
    secondary = secondaryColorLight,
    tertiary = thirdColorLight,
    onPrimary = textColorLight,
    onSecondary = textColorLight,
    onTertiary = textColorLight,
    outline = highLightedTextLight

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
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

         */
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current

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

        // setStatusBarColor() and setNavigationBarColor() also exist

        onDispose {}
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
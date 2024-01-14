package com.github.soulsearching.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

/**
 * Dynamic colors used in the application.
 */
object SoulSearchingColorTheme {
    var colorScheme = SoulSearchingPalette()

    private val darkTheme = SoulSearchingPalette()
    private val lightTheme = SoulSearchingPalette(
        primary = primaryColorLight,
        secondary = secondaryColorLight,
        onPrimary = textColorLight,
        onSecondary = textColorLight,
        subText = subTextColorLight
    )

    /**
     * Retrieve the correct SoulSearchingPalette based on the device theme.
     */
    val defaultTheme: SoulSearchingPalette
        @Composable
        get() = if (isSystemInDarkTheme()) darkTheme else lightTheme

}
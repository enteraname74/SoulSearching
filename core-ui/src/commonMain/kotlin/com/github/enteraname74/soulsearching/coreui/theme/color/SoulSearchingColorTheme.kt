package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*

/**
 * Dynamic colors used in the application.
 */
object SoulSearchingColorTheme {
    val colorScheme: SoulSearchingPalette
        @Composable
        get() = LocalColors.current

    val darkTheme = SoulSearchingPalette()
    val lightTheme = SoulSearchingPalette(
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

    fun fromTheme(isInDarkTheme: Boolean): SoulSearchingPalette =
        if (isInDarkTheme) darkTheme else lightTheme

}

val LocalColors = compositionLocalOf { SoulSearchingColorTheme.lightTheme }
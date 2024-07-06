package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Dynamic colors used in the application.
 */
object SoulSearchingColorTheme {
    var colorScheme by mutableStateOf(SoulSearchingPalette())

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
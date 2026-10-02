package com.github.enteraname74.soulsearching.coreui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Contains all elements related to a specific context of a SoulSearching application.
 */
expect object SoulSearchingContext {

    /**
     * Define the system bars color if there is any.
     */
    @Composable
    fun setSystemBarsColor(
        statusBarColor: Color,
        navigationBarColor: Color,
        isUsingDarkIcons: Boolean
    )
}

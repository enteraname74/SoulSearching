package com.github.enteraname74.soulsearching.coreui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

actual object SoulSearchingContext {

    /**
     * Define the system bars color if there is any.
     */
    @Composable
    actual fun setSystemBarsColor(
        statusBarColor: Color,
        navigationBarColor: Color,
        isUsingDarkIcons: Boolean
    ) {
        val systemUiController = rememberSystemUiController()

        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = isUsingDarkIcons
        )

        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = isUsingDarkIcons
        )
    }
}

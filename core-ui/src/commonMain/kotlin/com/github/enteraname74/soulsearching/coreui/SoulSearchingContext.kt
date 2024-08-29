package com.github.enteraname74.soulsearching.coreui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

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

    /**
     * Check the state of the read permission.
     */
    @Composable
    fun checkIfReadPermissionGranted(): Boolean

    /**
     * Check the state of the post notification permission.
     * If the device is below Android 13, the post notification is not necessary.
     */
    @Composable
    fun checkIfPostNotificationGranted(): Boolean
}
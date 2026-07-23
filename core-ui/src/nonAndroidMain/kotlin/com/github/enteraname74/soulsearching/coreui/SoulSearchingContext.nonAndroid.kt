package com.github.enteraname74.soulsearching.coreui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
        // Does nothing on desktop.
    }

    /**
     * Check the state of the read permission.
     */
    @Composable
    actual fun checkIfReadPermissionGranted(): Boolean {
        // No permissions are necessary on desktop.
        return true
    }

    /**
     * Check the state of the post notification permission.
     * If the device is below Android 13, the post notification is not necessary.
     */
    @Composable
    actual fun checkIfPostNotificationGranted(): Boolean {
        // No permissions are necessary on desktop.
        return true
    }
}

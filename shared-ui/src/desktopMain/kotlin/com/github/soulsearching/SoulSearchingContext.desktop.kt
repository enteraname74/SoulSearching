package com.github.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.github.soulsearching.types.ScreenOrientation

actual object SoulSearchingContext {
    actual val orientation: ScreenOrientation
        get() {
            val width = java.awt.Toolkit.getDefaultToolkit().screenSize.width
            return if (width >= 500) {
                ScreenOrientation.HORIZONTAL
            } else {
                ScreenOrientation.VERTICAL
            }
        }

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

    /**
     * Painter to use for accessing drawable resources.
     */
    @Composable
    actual fun painterResource(resourcePath: String): Painter {
        return painterResource(resourcePath)
    }
}
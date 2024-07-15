package com.github.enteraname74.soulsearching.coreui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi

actual object SoulSearchingContext {
    @OptIn(ExperimentalComposeUiApi::class)
    actual val orientation: ScreenOrientation
        @Composable
        get() {
            val density = LocalDensity.current
            val width = with(density) {
                LocalWindowInfo.current.containerSize.width.toDp()
            }
            return if (width >= 600.dp) {
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
}
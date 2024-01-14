package com.github.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalWindowInfo
import com.github.soulsearching.types.ScreenOrientation
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

actual object SoulSearchingContext {
    @OptIn(ExperimentalComposeUiApi::class)
    actual val orientation: ScreenOrientation
        @Composable
        get() {
            val width = LocalWindowInfo.current.containerSize.width
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
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    actual fun appPainterResource(resourcePath: String): Painter {
        return painterResource(resourcePath)
    }
}
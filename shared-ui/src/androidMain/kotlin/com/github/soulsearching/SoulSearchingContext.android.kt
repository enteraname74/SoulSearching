package com.github.soulsearching

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.github.soulsearching.types.ScreenOrientation
import com.google.accompanist.systemuicontroller.rememberSystemUiController


actual object SoulSearchingContext {
    actual val orientation: ScreenOrientation
        @Composable
        get() {
            val orientation = LocalConfiguration.current.orientation
            return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
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

    /**
     * Check the state of the read permission.
     */
    @Composable
    actual fun checkIfReadPermissionGranted(): Boolean {
        val context = LocalContext.current
        return if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check the state of the post notification permission.
     * If the device is below Android 13, the post notification is not necessary.
     */
    @Composable
    actual fun checkIfPostNotificationGranted(): Boolean {
        val context = LocalContext.current
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    /**
     * Painter to use for accessing drawable resources.
     */
    @SuppressLint("DiscouragedApi")
    @Composable
    actual fun painterResource(resourcePath: String): Painter {
        val context = LocalContext.current
        val drawableId = remember(resourcePath) {
            context.resources.getIdentifier(
                resourcePath,
                "drawable",
                context.packageName
            )
        }
        return painterResource(id = drawableId)
    }

}
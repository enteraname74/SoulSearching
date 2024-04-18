package com.github.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.github.soulsearching.domain.model.types.ScreenOrientation

/**
 * Contains all elements related to a specific context of a SoulSearching application.
 */
expect object SoulSearchingContext {
    /**
     * Painter to use for accessing drawable resources.
     */
    @Composable
    fun appPainterResource(resourcePath: String): Painter

    @get:Composable
    val orientation: ScreenOrientation

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
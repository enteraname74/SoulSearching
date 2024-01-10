package com.github.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import com.github.soulsearching.types.ScreenOrientation

/**
 * Contains all elements related to the context of a SoulSearching application.
 */
expect object SoulSearchingContext {
    /**
     * Painter for the default SoulSearching logo.
     */
    @get:Composable
    val defaultLogoPainter: Painter

    @Composable
    fun getScreenOrientation(): ScreenOrientation
}
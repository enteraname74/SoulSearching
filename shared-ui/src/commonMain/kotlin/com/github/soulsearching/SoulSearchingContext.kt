package com.github.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Contains all elements related to the context of a SoulSearching application.
 */
expect object SoulSearchingContext {
    /**
     * Painter for the default SoulSearching logo.
     */
    @get:Composable
    val defaultLogoPainter: Painter
}
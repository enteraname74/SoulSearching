package com.github.enteraname74.soulsearching.coreui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Constants used throughout the application.
 */
object UiConstants {

    object Player {
        val TopPanelSize: Float
            @Composable
            get() = with(LocalDensity.current) { 64.dp.toPx() }

        val playerPlayerButtonSize: Dp = 78.dp
    }

    /**
     * Spacing values for spacing or simple size.
     */
    object Spacing {
        val small: Dp = 4.dp
        val medium: Dp = 8.dp
        val large: Dp = 16.dp
        val veryLarge: Dp = 24.dp
        val huge: Dp = 32.dp
        val veryHuge: Dp = 48.dp
    }

    /**
     * Define the sizes of images in the application.
     */
    object ImageSize {
        val small: Dp = 16.dp
        val medium: Dp = 32.dp
        val large: Dp = 64.dp
        val veryLarge: Dp = 128.dp
        val huge: Dp = 160.dp
        val veryHuge: Dp = 200.dp
    }

    /**
     * Define the sizes of cover of elements (playlist, music...) in the application.
     */
    object CoverSize {
        val small: Dp = 55.dp
        val huge: Dp = 200.dp
    }

    /**
     * Define the durations of animations
     */
    object AnimationDuration {
        const val short: Int = 100
        const val normal: Int = 300
    }

    object Typography {
        val bodyTitle: TextStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight(500),
        )
        val bodyMediumTitle: TextStyle = TextStyle(
            fontSize = 18.sp,
        )
        val bodySmall: TextStyle
            @Composable
            get() = MaterialTheme.typography.bodyMedium
    }
}
package com.github.enteraname74.soulsearching.coreui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.ext.toPx
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

/**
 * Constants used throughout the application.
 */
object UiConstants {

    object Player {
        val playerPlayerButtonSize: Dp = 78.dp
    }

    /**
     * Spacing values for spacing or simple size.
     */
    object Spacing {
        val small: Dp = 4.dp
        val medium: Dp = 8.dp
        val mediumPlus: Dp = 12.dp
        val large: Dp = 16.dp
        val veryLarge: Dp = 24.dp
        val huge: Dp = 32.dp
        val veryHuge: Dp = 48.dp
    }

    /**
     * Define the sizes of images in the application.
     */
    object ImageSize {
        val verySmall: Dp = 12.dp
        val small: Dp = 16.dp
        val smallPlus: Dp = 24.dp
        val medium: Dp = 32.dp
        val mediumPlus: Dp = 40.dp
        val large: Dp = 64.dp
        val veryLarge: Dp
        @Composable
        get() {
            val windowSize = rememberWindowSize()
            return if (windowSize == WindowSize.Large) {
                148.dp
            } else {
                128.dp
            }
        }
        val huge: Dp = 160.dp

        val veryHuge: Dp
            @Composable
            get() {
                val windowSize = rememberWindowSize()
                return if (windowSize == WindowSize.Large) {
                    300.dp
                } else {
                    200.dp
                }
            }
    }

    /**
     * Define the sizes of cover of elements (playlist, music...) in the application.
     */
    object CoverSize {
        val small: Dp
        @Composable
        get() {
            val windowSize = rememberWindowSize()
            return if (windowSize == WindowSize.Large) {
                65.dp
            } else {
                55.dp
            }
        }
        val medium: Dp = 80.dp
        val huge: Dp = 200.dp
    }

    /**
     * Define the durations of animations
     */
    object AnimationDuration {
        const val short: Int = 100
        const val medium: Int = 200
        const val normal: Int = 300
    }

    object Typography {
        val titleSmall: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        val bodyTitle: TextStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight(500),
        )
        val bodyMediumTitle: TextStyle = TextStyle(
            fontSize = 18.sp,
        )
        val body: TextStyle = TextStyle(
            fontSize = 15.sp,
        )
        val bodySmall: TextStyle
            @Composable
            get() = MaterialTheme.typography.bodyMedium
    }
}
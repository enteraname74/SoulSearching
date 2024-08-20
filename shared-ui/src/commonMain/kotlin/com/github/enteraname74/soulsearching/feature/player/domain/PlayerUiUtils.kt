package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeightDp
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

object PlayerUiUtils {
    private val minHeightForRowView: Dp = 500.dp

    @Composable
    fun canShowRowControlPanel(): Boolean {
        val windowSize = rememberWindowSize()
        val maxHeightDp = rememberWindowHeightDp()
        return windowSize != WindowSize.Small && maxHeightDp <= minHeightForRowView
    }

    @Composable
    fun canShowSidePanel(): Boolean {
        val windowSize = rememberWindowSize()
        val maxHeightDp = rememberWindowHeightDp()

        // If we are on the large window size, we show it no matter the height.
        if (windowSize == WindowSize.Large) {
            return true
        }

        /*
        If the height of the screen is too small, we do not show the side panel.
         */
        if (maxHeightDp <= minHeightForRowView) {
            return false
        }

        /*
        Else, we only show it on large window size
         */
        return windowSize != WindowSize.Small
    }

    @Composable
    fun getDraggablePanelCollapsedOffset(): Float {
        val maxHeightDp = rememberWindowHeightDp()
        return if (maxHeightDp <= minHeightForRowView) {
            0f
        } else {
            UiConstants.Player.TopPanelSize
        }
    }
}
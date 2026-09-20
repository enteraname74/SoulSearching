package com.github.enteraname74.soulsearching.composables.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

object NavigationPanelUiUtils {
    val PanelWidth: Dp = 300.dp

    @Composable
    fun canShowPanel(): Boolean {
        val windowSize = rememberWindowSize()

        return windowSize == WindowSize.Large
    }
}
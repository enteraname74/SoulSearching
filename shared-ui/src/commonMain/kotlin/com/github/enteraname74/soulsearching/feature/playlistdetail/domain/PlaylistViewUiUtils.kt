package com.github.enteraname74.soulsearching.feature.playlistdetail.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeightDp

object PlaylistViewUiUtils {
    private val minHeightForSmallView: Dp = 400.dp

    @Composable
    fun canShowVerticalMainInformation(): Boolean {
        val maxHeight = rememberWindowHeightDp()
        return maxHeight > minHeightForSmallView
    }
}
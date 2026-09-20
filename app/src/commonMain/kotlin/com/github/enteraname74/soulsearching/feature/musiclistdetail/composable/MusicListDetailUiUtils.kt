package com.github.enteraname74.soulsearching.feature.musiclistdetail.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeightDp

object MusicListDetailUiUtils {
    private val minHeightForSmallView: Dp = 500.dp

    @Composable
    fun canShowColumnLayout(): Boolean {
        val maxHeight = rememberWindowHeightDp()
        return maxHeight > minHeightForSmallView
    }
}
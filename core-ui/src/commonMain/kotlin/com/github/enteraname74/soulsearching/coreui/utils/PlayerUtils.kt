package com.github.enteraname74.soulsearching.coreui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.ext.toPx

val PlayerMinimisedHeight: Float
    @Composable
    get() {
        return PlayerBasedMinimisedHeight.toPx() + getNavigationBarPadding().toFloat()
    }

private val PlayerBasedMinimisedHeight: Dp = 70.dp
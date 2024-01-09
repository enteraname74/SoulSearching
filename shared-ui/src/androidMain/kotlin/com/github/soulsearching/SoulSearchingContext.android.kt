package com.github.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource


actual object SoulSearchingContext {
    actual val defaultLogoPainter: Painter
        @Composable
        get() = painterResource(id = R.drawable.ic_saxophone_svg)
}
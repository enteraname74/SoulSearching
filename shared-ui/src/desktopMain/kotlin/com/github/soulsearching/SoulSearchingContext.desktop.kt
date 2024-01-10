package com.github.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.github.soulsearching.types.ScreenOrientation

actual object SoulSearchingContext {
    actual val defaultLogoPainter: Painter
        @Composable
        get() = painterResource("saxophone_svg.svg")

    @Composable
    actual fun getScreenOrientation(): ScreenOrientation {
        val width = java.awt.Toolkit.getDefaultToolkit().screenSize.width
        return if (width >= 500) {
            ScreenOrientation.HORIZONTAL
        } else {
            ScreenOrientation.VERTICAL
        }
    }
}
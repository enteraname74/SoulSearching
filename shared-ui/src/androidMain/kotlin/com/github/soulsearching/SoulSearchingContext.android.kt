package com.github.soulsearching

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import com.github.soulsearching.types.ScreenOrientation


actual object SoulSearchingContext {
    actual val defaultLogoPainter: Painter
        @Composable
        get() = painterResource(id = R.drawable.ic_saxophone_svg)

    @Composable
    actual fun getScreenOrientation(): ScreenOrientation {
        val orientation = LocalConfiguration.current.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ScreenOrientation.HORIZONTAL
        } else {
            ScreenOrientation.VERTICAL
        }
    }
}
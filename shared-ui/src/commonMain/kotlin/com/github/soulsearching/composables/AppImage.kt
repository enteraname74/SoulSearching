package com.github.soulsearching.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.strings.strings
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.Drawables

@Composable
fun AppImage(
    bitmap : ImageBitmap?,
    size : Dp,
    modifier: Modifier = Modifier,
    roundedPercent : Int = 10,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    val modifierBase = Modifier
        .size(size)
        .clip(RoundedCornerShape(percent = roundedPercent))
        .composed {
            modifier
        }

    if (bitmap != null) {
        Image(
            modifier = modifierBase,
            bitmap = bitmap,
            contentDescription = strings.image,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            modifier = modifierBase,
            painter = SoulSearchingContext.appPainterResource(Drawables.appIcon),
            contentDescription = strings.image,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(tint)
        )
    }

}
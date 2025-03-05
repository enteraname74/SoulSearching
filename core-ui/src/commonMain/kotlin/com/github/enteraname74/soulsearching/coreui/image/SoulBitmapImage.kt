package com.github.enteraname74.soulsearching.coreui.image

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
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.app_icon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SoulBitmapImage(
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
            painter = painterResource(Res.drawable.app_icon),
            contentDescription = strings.image,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(tint)
        )
    }
}
package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulByteArrayImage(
    data: ByteArray?,
    size: Dp,
    modifier: Modifier = Modifier,
    roundedPercent: Int = 10,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    val modifierBase = Modifier
        .size(size)
        .clip(RoundedCornerShape(percent = roundedPercent))
        .composed {
            modifier
        }

    AnimatedImage(
        data = data,
        contentScale = ContentScale.Crop,
        modifier = modifierBase,
        tint = tint,
    ) { byteArray ->
        DataImage(
            data = byteArray,
            modifier = modifierBase,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            tint = tint,
        )
    }
}
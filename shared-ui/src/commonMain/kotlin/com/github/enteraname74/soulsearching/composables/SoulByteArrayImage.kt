package com.github.enteraname74.soulsearching.composables

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
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SoulByteArrayImage(
    data: ByteArray?,
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

    if (data != null) {
        DataImage(
            data = data,
            modifier = modifierBase,
            contentScale = ContentScale.Crop,
        )
    } else {
        TemplateImage(
            modifier = modifierBase,
            contentScale = ContentScale.Crop,
            tint = tint,
        )
    }
}
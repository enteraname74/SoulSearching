package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.saxophone_png
import org.jetbrains.compose.resources.painterResource

@Composable
fun TemplateImage(
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color,
) {
    Image(
        modifier = modifier,
        painter = painterResource(Res.drawable.saxophone_png),
        contentDescription = strings.image,
        contentScale = contentScale,
        colorFilter = ColorFilter.tint(tint)
    )
}
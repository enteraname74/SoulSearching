package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.toUri
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.saxophone_png
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun CoverPathImage(
    initialCoverPath: String?,
    modifier: Modifier,
    tint: Color,
    contentScale: ContentScale,
) {
    println("COVER PATH: $initialCoverPath")
    if (initialCoverPath != null) {
        AsyncImage(
            error = painterResource(Res.drawable.saxophone_png),
            placeholder = painterResource(Res.drawable.saxophone_png),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(initialCoverPath.toUri())
                .build(),
            contentDescription = "",
            modifier = modifier,
            contentScale = contentScale,
        )
    } else {
        Image(
            modifier = modifier,
            painter = painterResource(Res.drawable.saxophone_png),
            contentDescription = strings.image,
            contentScale = contentScale,
            colorFilter = ColorFilter.tint(tint)
        )
    }
}
package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.request.ImageRequest
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
internal fun UrlImage(
    url: String?,
    modifier: Modifier = Modifier,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String?,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    AnimatedImage(
        data = url,
        contentScale = contentScale,
        modifier = modifier,
        tint = tint,
    ) { foundUrl ->
        DataImage(
            data = foundUrl,
            contentScale = contentScale,
            modifier = modifier,
            onSuccess = onSuccess,
            builderOptions = builderOptions,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
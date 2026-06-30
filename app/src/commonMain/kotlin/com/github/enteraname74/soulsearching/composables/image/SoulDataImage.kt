package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.BitmapImage
import coil3.Image
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.enteraname74.soulsearching.app.generated.resources.Res
import com.github.enteraname74.soulsearching.app.generated.resources.app_logo_uni_xml
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun SoulDataImage(
    data: Any?,
    modifier: Modifier,
    contentScale: ContentScale,
    contentDescription: String?,
    tint: Color,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    crossfade: Boolean = true,
    showPlaceholder: Boolean = true,
) {
    var previousSavedImage: Image? by remember {
        mutableStateOf(null)
    }

    AsyncImage(
        onSuccess = { result ->
            if (result.result.image != previousSavedImage) {
                previousSavedImage = result.result.image
                onSuccess?.let {
                    it((result.result.image as? BitmapImage)?.bitmap?.asImageBitmap())
                }
            }
        },
        onError = {
            onSuccess?.let { onSuccess ->
                previousSavedImage = null
                onSuccess(null)
            }
        },
        placeholder = if (showPlaceholder) {
            forwardingPainter(
                painter = painterResource(Res.drawable.app_logo_uni_xml),
                colorFilter = ColorFilter.tint(tint),
            )
        } else {
            null
        },
        error = forwardingPainter(
            painter = painterResource(Res.drawable.app_logo_uni_xml),
            colorFilter = ColorFilter.tint(tint),
        ),
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .builderOptions()
            .data(data)
            .crossfade(crossfade)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}

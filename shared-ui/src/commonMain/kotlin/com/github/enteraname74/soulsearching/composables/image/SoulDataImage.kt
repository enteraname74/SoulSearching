package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.BitmapImage
import coil3.Image
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.saxophone_png
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DataImage(
    data: Any?,
    modifier: Modifier,
    contentScale: ContentScale,
    contentDescription: String?,
    tint: Color,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
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
            println("ERROR: $it")
            onSuccess?.let {
                previousSavedImage = null
                it(null)
            }
        },
        placeholder = painterResource(Res.drawable.saxophone_png),
        colorFilter = if (previousSavedImage != null) {
            null
        } else {
            ColorFilter.tint(tint)
        },
        error = painterResource(Res.drawable.saxophone_png),
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .builderOptions()
            .data(data)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}
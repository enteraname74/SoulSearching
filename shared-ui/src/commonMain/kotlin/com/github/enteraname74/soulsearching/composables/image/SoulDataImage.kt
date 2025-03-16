package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.BitmapImage
import coil3.Image
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.app_logo_uni_xml
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
            onSuccess?.let {
                previousSavedImage = null
                it(null)
            }
        },
        placeholder = forwardingPainter(
            painter = painterResource(Res.drawable.app_logo_uni_xml),
            colorFilter = ColorFilter.tint(tint),
        ),
        error = forwardingPainter(
            painter = painterResource(Res.drawable.app_logo_uni_xml),
            colorFilter = ColorFilter.tint(tint),
        ),
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

private fun forwardingPainter(
    painter: Painter,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onDraw: DrawScope.(ForwardingDrawInfo) -> Unit = DefaultOnDraw,
): Painter = ForwardingPainter(painter, alpha, colorFilter, onDraw)

private data class ForwardingDrawInfo(
    val painter: Painter,
    val alpha: Float,
    val colorFilter: ColorFilter?,
)

private class ForwardingPainter(
    private val painter: Painter,
    private var alpha: Float,
    private var colorFilter: ColorFilter?,
    private val onDraw: DrawScope.(ForwardingDrawInfo) -> Unit,
) : Painter() {

    private var info = newInfo()

    override val intrinsicSize get() = painter.intrinsicSize

    override fun applyAlpha(alpha: Float): Boolean {
        if (alpha != DefaultAlpha) {
            this.alpha = alpha
            this.info = newInfo()
        }
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        if (colorFilter != null) {
            this.colorFilter = colorFilter
            this.info = newInfo()
        }
        return true
    }

    override fun DrawScope.onDraw() = onDraw(info)

    private fun newInfo() = ForwardingDrawInfo(painter, alpha, colorFilter)
}

private val DefaultOnDraw: DrawScope.(ForwardingDrawInfo) -> Unit = { info ->
    with(info.painter) {
        draw(size, info.alpha, info.colorFilter)
    }
}
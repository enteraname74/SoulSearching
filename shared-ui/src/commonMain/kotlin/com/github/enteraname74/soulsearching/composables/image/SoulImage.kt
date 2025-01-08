package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.request.ImageRequest
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulImage(
    cover: Cover?,
    size: Dp?,
    modifier: Modifier = Modifier,
    roundedPercent: Int = 10,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {

    val sizeModifier = if (size != null) {
        Modifier.size(size)
    } else {
        Modifier
    }

    val baseModifier = Modifier
        .then(sizeModifier)
        .clip(RoundedCornerShape(percent = roundedPercent))
        .then(modifier)

    println("COVER: $cover")
    when (cover) {
        null -> {
            TemplateImage(
                modifier = baseModifier,
                contentScale = contentScale,
                tint = tint,
            )
        }

        is Cover.CoverFile -> {
            FileCover(
                cover = cover,
                modifier = baseModifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
                contentDescription = null,
            )
        }

        is Cover.CoverUrl -> {
            UrlImage(
                url = cover.url,
                modifier = baseModifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
                contentDescription = null,
            )
        }
    }
}







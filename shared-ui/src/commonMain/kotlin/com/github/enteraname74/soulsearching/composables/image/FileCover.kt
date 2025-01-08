package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.request.ImageRequest
import com.github.enteraname74.domain.model.Cover

@Composable
internal fun FileCover(
    cover: Cover.CoverFile,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color,
    contentDescription: String?,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    when {
        cover.fileCoverId != null -> {
            CoverIdImage(
                coverId = cover.fileCoverId,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
                contentDescription = contentDescription,
            )
        }

        cover.initialCoverPath != null -> {
            MusicFileImage(
                musicPath = cover.initialCoverPath!!,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
            )
        }

        else -> {
            TemplateImage(
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
            )
        }
    }
}
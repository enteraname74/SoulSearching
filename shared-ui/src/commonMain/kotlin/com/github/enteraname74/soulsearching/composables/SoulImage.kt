package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.BitmapImage
import coil3.Image
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.util.CoverFileManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.saxophone_png
import org.jetbrains.compose.resources.painterResource
import java.util.*

@Composable
fun SoulImage(
    cover: Cover?,
    size: Dp,
    modifier: Modifier = Modifier,
    roundedPercent: Int = 10,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    val baseModifier = Modifier
        .size(size)
        .clip(RoundedCornerShape(percent = roundedPercent))
        .then(modifier)

    when (cover) {
        null -> {
            TemplateImage(
                modifier = baseModifier,
                contentScale = contentScale,
                tint = tint,
            )
        }

        is Cover.FileCover -> {
            FileCover(
                cover = cover,
                modifier = baseModifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
            )
        }
    }
}

@Composable
private fun FileCover(
    cover: Cover.FileCover,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    when {
        cover.isEmpty() -> {
            TemplateImage(
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
            )
        }

        cover.fileCoverId != null -> {
            CoverIdImage(
                coverId = cover.fileCoverId,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
                builderOptions = builderOptions,
            )
        }

        cover.initialCoverPath != null -> {
            CoverPathImage(
                initialCoverPath = cover.initialCoverPath,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
            )
        }
    }
}

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

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DataImage(
    data: Any?,
    modifier: Modifier,
    contentScale: ContentScale,
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
        placeholder = painterResource(Res.drawable.saxophone_png),
        error = painterResource(Res.drawable.saxophone_png),
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .builderOptions()
            .data(data)
            .crossfade(true)
            .build(),
        contentDescription = "",
        modifier = modifier,
        contentScale = contentScale,
    )
}

@Composable
private fun CoverIdImage(
    coverId: UUID?,
    modifier: Modifier = Modifier,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {

    val coverFileManager: CoverFileManager = injectElement()

    var coverPath: String? by rememberSaveable {
        mutableStateOf(null)
    }

    LaunchedEffect(coverId) {
        if (coverId == null) {
            coverPath = null
            return@LaunchedEffect
        }
        coverPath = coverFileManager.getCoverPath(id = coverId)
    }

    if (coverPath != null) {
        DataImage(
            data = coverPath,
            modifier = modifier,
            contentScale = contentScale,
            builderOptions = builderOptions,
        )
    } else {
        TemplateImage(
            modifier = modifier,
            contentScale = contentScale,
            tint = tint,
        )
    }
}

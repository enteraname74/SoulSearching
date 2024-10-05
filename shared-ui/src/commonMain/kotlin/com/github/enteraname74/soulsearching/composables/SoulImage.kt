package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.util.CoverFileManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.saxophone_png
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.util.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SoulImage(
    cover: Cover?,
    size: Dp,
    modifier: Modifier = Modifier,
    roundedPercent: Int = 10,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
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
            )
        }

        cover.initialCoverPath != null -> {
            CoverPathImage(
                initialCoverPath = cover.initialCoverPath,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
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

@Composable
fun DataImage(
    data: Any?,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    AsyncImage(
        error = painterResource(Res.drawable.saxophone_png),
        model = ImageRequest.Builder(LocalPlatformContext.current)
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
        )
    } else {
        TemplateImage(
            modifier = modifier,
            contentScale = contentScale,
            tint = tint,
        )
    }
}

package com.github.enteraname74.soulsearching.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import coil3.request.ImageRequest
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.soulsearching.composables.image.SoulDataImage
import com.github.enteraname74.soulsearching.composables.image.UrlImage
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.app_logo_uni_xml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.util.*

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
    InnerSoulImage(
        cover = cover,
        modifier = if (size != null) {
            Modifier.size(size)
        } else {
            Modifier
        }
            .clip(RoundedCornerShape(percent = roundedPercent))
            .then(modifier),
        tint = tint,
        contentScale = contentScale,
        onSuccess = onSuccess,
        builderOptions = builderOptions,
    )
}

@Composable
fun SoulImage(
    cover: Cover?,
    size: DpSize?,
    modifier: Modifier = Modifier,
    roundedPercent: Int = 10,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    InnerSoulImage(
        cover = cover,
        modifier = if (size != null) {
            Modifier.size(size)
        } else {
            Modifier
        }
            .clip(RoundedCornerShape(percent = roundedPercent))
            .then(modifier),
        tint = tint,
        contentScale = contentScale,
        onSuccess = onSuccess,
        builderOptions = builderOptions,
    )
}

@Composable
fun InnerSoulImage(
    cover: Cover?,
    modifier: Modifier,
    tint: Color,
    contentScale: ContentScale,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)?,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder,
) {
    when (cover) {
        null -> {
            TemplateImage(
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
            )
        }

        is Cover.CoverFile -> {
            FileCover(
                cover = cover,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
            )
        }
        is Cover.CoverUrl -> {
            UrlImage(
                url = cover.url,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun FileCover(
    cover: Cover.CoverFile,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color,
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

@Composable
private fun MusicFileImage(
    musicPath: String,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    coverUtils: CachedCoverManager = injectElement(),
) {
    var fileData: ImageBitmap? by remember {
        mutableStateOf(
            coverUtils.getCachedImage(musicPath)
        )
    }
    var job: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(musicPath) {
        if (job?.isActive == true) {
            return@LaunchedEffect
        }

        job = CoroutineScope(Dispatchers.IO).launch {
            val fetchedCover = coverUtils.fetchCoverOfMusicFile(musicPath = musicPath)
            fileData = fetchedCover
            onSuccess?.let { it(fetchedCover) }
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = fileData != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            if (fileData != null) {
                Image(
                    bitmap = fileData!!,
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = contentScale,
                )
            }
        }

        AnimatedVisibility(
            visible = fileData == null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            TemplateImage(
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
        painter = painterResource(Res.drawable.app_logo_uni_xml),
        contentDescription = strings.image,
        contentScale = contentScale,
        colorFilter = ColorFilter.tint(tint)
    )
}

@Composable
private fun CoverIdImage(
    coverId: UUID?,
    modifier: Modifier = Modifier,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
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
        coverPath = coverFileManager.getPath(id = coverId)
    }

    if (coverPath != null) {
        SoulDataImage(
            data = coverPath,
            modifier = modifier,
            contentScale = contentScale,
            builderOptions = builderOptions,
            onSuccess = onSuccess,
            tint = tint,
            contentDescription = null,
        )
    } else {
        TemplateImage(
            modifier = modifier,
            contentScale = contentScale,
            tint = tint,
        )
    }
}
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import coil3.BitmapImage
import coil3.Image
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.app.generated.resources.Res
import com.github.enteraname74.soulsearching.app.generated.resources.app_logo_uni_xml
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.serialization.SerializationUtils
import com.github.enteraname74.soulsearching.util.FileOperation
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
        cover.devicePathSpec != null -> {
            CoverFolderImage(
                devicePathSpec = cover.devicePathSpec!!,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
            )
        }

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
private fun CoverFolderImage(
    devicePathSpec: Cover.CoverFile.DevicePathSpec,
    modifier: Modifier,
    contentScale: ContentScale,
    tint: Color,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder,
    settings: SoulSearchingSettings = injectElement(),
    fileOperation: FileOperation = injectElement()
) {
    var inputStream: String? by rememberSaveable {
        mutableStateOf(null)
    }
    var shouldUseFallback: Boolean by rememberSaveable { mutableStateOf(false) }
    var job: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(devicePathSpec) {
        if (job?.isActive == true) {
            return@LaunchedEffect
        }

        job = CoroutineScope(Dispatchers.IO).launch {
            inputStream = settings.get(SoulSearchingSettingsKeys.Cover.ARTIST_COVER_FOLDER_RETRIEVER)
                .takeIf { it.isNotBlank() }?.let {
                    runCatching {
                        val coverFolderRetriever: CoverFolderRetriever = SerializationUtils.deserialize(it)
                        coverFolderRetriever.buildSafeDynamicCoverPath(
                            dynamicName = devicePathSpec.dynamicElementName,
                            safeBuilder = fileOperation::buildSafePath,
                        )
                    }.getOrNull()
                }
            shouldUseFallback = inputStream == null
        }
    }

    when {
        shouldUseFallback && (devicePathSpec.fallback as? Cover.CoverFile) != null -> {
            FileCover(
                cover = devicePathSpec.fallback as Cover.CoverFile,
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
                onSuccess = onSuccess,
                builderOptions = builderOptions,
            )
        }

        inputStream == null -> {
            TemplateImage(
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
            )
        }

        else -> {
            DataImage(
                data = inputStream,
                modifier = modifier,
                contentScale = contentScale,
                builderOptions = builderOptions,
                onSuccess = { bitmap ->
                    if (bitmap == null) {
                        shouldUseFallback = true
                    }
                    onSuccess?.invoke(bitmap)
                },
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
fun DataImage(
    data: Any?,
    modifier: Modifier,
    tint: Color,
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
        coverPath = coverFileManager.getCoverPath(id = coverId)
    }

    if (coverPath != null) {
        DataImage(
            data = coverPath,
            modifier = modifier,
            contentScale = contentScale,
            builderOptions = builderOptions,
            onSuccess = onSuccess,
            tint = tint,
        )
    } else {
        TemplateImage(
            modifier = modifier,
            contentScale = contentScale,
            tint = tint,
        )
    }
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

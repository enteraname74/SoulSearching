package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.request.ImageRequest
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import java.util.*

@Composable
internal fun CoverIdImage(
    coverId: UUID?,
    modifier: Modifier = Modifier,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String?,
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

    AnimatedImage(
        data = coverPath,
        modifier = modifier,
        contentScale = contentScale,
        tint = tint,
    ) {
        DataImage(
            data = coverPath,
            modifier = modifier,
            contentScale = contentScale,
            builderOptions = builderOptions,
            onSuccess = onSuccess,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
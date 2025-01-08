package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
internal fun MusicFileImage(
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

    AnimatedImage(
        data = fileData,
        contentScale = contentScale,
        modifier = modifier,
        tint = tint,
    ) { imageBitmap ->
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}
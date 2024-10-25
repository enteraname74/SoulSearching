package com.github.enteraname74.soulsearching.features.filemanager.cover

import android.media.MediaMetadataRetriever
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

class CachedCoverManagerAndroidImpl : CachedCoverManager() {
    @OptIn(ExperimentalResourceApi::class)
    override fun fetchCoverOfMusicFile(musicPath: String): ImageBitmap? =
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(musicPath)
            val data: ImageBitmap? = mediaMetadataRetriever.embeddedPicture?.decodeToImageBitmap()
            data?.let { cachedImages[musicPath] = it }
            data
        } catch (e: Exception) {
            println("CoverUtilsImpl -- Error while fetching song's cover: ${e.message}")
            null
        }
}
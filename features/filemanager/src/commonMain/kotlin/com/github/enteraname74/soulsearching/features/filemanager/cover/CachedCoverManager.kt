package com.github.enteraname74.soulsearching.features.filemanager.cover

import androidx.compose.ui.graphics.ImageBitmap
import org.koin.core.component.KoinComponent

abstract class CachedCoverManager: KoinComponent {
    protected val cachedImages: HashMap<String, ImageBitmap> = hashMapOf()
    protected val cachedImagesByteArray: HashMap<String, ByteArray> = hashMapOf()

    fun getCachedImage(key: String): ImageBitmap? =
        cachedImages[key]

    fun getCachedImageByteArray(key: String): ByteArray? =
        cachedImagesByteArray[key]

    abstract suspend fun fetchCoverOfMusicFile(musicPath: String): ImageBitmap?
    abstract suspend fun fetchCoverOfMusicFileAsByteArray(musicPath: String): ByteArray?
}
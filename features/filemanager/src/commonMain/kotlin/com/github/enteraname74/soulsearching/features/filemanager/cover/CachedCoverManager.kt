package com.github.enteraname74.soulsearching.features.filemanager.cover

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter

abstract class CachedCoverManager {
    protected val cachedImages: HashMap<String, ImageBitmap> = hashMapOf()
    protected val cachedImagesByteArray: HashMap<String, ByteArray> = hashMapOf()
    private val cachedPlaceholders: HashMap<String, Painter> = hashMapOf()

    fun getCachedImage(key: String): ImageBitmap? =
        cachedImages[key]

    fun getCachedImageByteArray(key: String): ByteArray? =
        cachedImagesByteArray[key]

    fun getCachedPlaceholder(key: String): Painter? =
        cachedPlaceholders[key]

    fun savePlaceholder(key: String, placeholder: Painter) {
        cachedPlaceholders[key] = placeholder
    }

    abstract suspend fun fetchCoverOfMusicFile(musicPath: String): ImageBitmap?
    abstract suspend fun fetchCoverOfMusicFileAsByteArray(musicPath: String): ByteArray?
}
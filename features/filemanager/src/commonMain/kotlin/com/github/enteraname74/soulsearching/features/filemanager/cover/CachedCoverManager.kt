package com.github.enteraname74.soulsearching.features.filemanager.cover

import androidx.compose.ui.graphics.ImageBitmap

abstract class CachedCoverManager {
    protected val cachedImages: HashMap<String, ImageBitmap> = hashMapOf()

    fun getCachedImage(key: String): ImageBitmap? =
        cachedImages[key]

    abstract fun fetchCoverOfMusicFile(musicPath: String): ImageBitmap?
}
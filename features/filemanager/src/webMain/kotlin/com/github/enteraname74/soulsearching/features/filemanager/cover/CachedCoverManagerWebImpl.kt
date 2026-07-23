package com.github.enteraname74.soulsearching.features.filemanager.cover

import androidx.compose.ui.graphics.ImageBitmap

internal class CachedCoverManagerWebImpl : CachedCoverManager() {
    override suspend fun fetchCoverOfMusicFile(musicPath: String): ImageBitmap? = null

    override suspend fun fetchCoverOfMusicFileAsByteArray(musicPath: String): ByteArray? = null
}

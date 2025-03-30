package com.github.enteraname74.soulsearching.features.filemanager.cover

import androidx.compose.ui.graphics.ImageBitmap
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class CachedCoverManager: KoinComponent {
    protected val cachedImages: HashMap<String, ImageBitmap> = hashMapOf()
    protected val cachedImagesByteArray: HashMap<String, ByteArray> = hashMapOf()
    private val settings: SoulSearchingSettings by inject()

    fun getCachedImage(key: String): ImageBitmap? =
        cachedImages[key]

    fun getCachedImageByteArray(key: String): ByteArray? =
        cachedImagesByteArray[key]

    abstract suspend fun fetchCoverOfMusicFile(musicPath: String): ImageBitmap?
    abstract suspend fun fetchCoverOfMusicFileAsByteArray(musicPath: String): ByteArray?

    fun clearUrlCachedImage(cover: Cover.CoverUrl, context: PlatformContext) {
        val imageLoader = SingletonImageLoader.get(context)
        cover.url?.let {
            val host: String = settings.get(SoulSearchingSettingsKeys.Cloud.HOST)
            val url = "$host/$it"

            val diskResult = imageLoader.diskCache?.remove(url)
            val memoryResult = imageLoader.memoryCache?.remove(MemoryCache.Key(url))
        }
    }
}
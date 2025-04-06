package com.github.enteraname74.soulsearching.features.filemanager.cover

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.features.httpclient.safeReadBytes
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

class CoverRetriever(
    private val cachedCoverManager: CachedCoverManager,
    private val coverFileManager: CoverFileManager,
    private val settings: SoulSearchingSettings,
    private val httpClient: HttpClient,
) {
    private suspend fun getRemoteImage(cover: Cover.CoverUrl): ByteArray? {
        if (cover.url == null) return null

        val url: String = if (cover.isFromCloud()) {
            val host = settings.get(SoulSearchingSettingsKeys.Cloud.HOST)
            "$host/${cover.url}"
        } else {
            cover.url!!
        }

        return httpClient.safeReadBytes {
            get(urlString = url)
        }.getOrNull()
    }

    private suspend fun getCoverByteArray(cover: Cover): ByteArray? {
        return when(cover) {
            is Cover.CoverFile -> {
                when {
                    cover.fileCoverId != null -> {
                        coverFileManager.getFileData(
                            fileId = cover.fileCoverId!!,
                        )
                    }

                    cover.initialCoverPath != null -> {
                        cachedCoverManager.getCachedImageByteArray(
                            key = cover.initialCoverPath!!,
                        ) ?: cachedCoverManager.fetchCoverOfMusicFileAsByteArray(
                            musicPath = cover.initialCoverPath!!,
                        )
                    }

                    else -> null
                }
            }

            is Cover.CoverUrl -> getRemoteImage(cover)
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun getImageBitmap(cover: Cover): ImageBitmap? =
        when(cover) {
            is Cover.CoverFile -> {
                when {
                    cover.fileCoverId != null -> {
                        coverFileManager.getFileData(
                            fileId = cover.fileCoverId!!,
                        )?.decodeToImageBitmap()
                    }
                    cover.initialCoverPath != null -> {
                        cachedCoverManager.getCachedImage(
                            key = cover.initialCoverPath!!,
                        ) ?: cachedCoverManager.fetchCoverOfMusicFile(
                            musicPath = cover.initialCoverPath!!,
                        )
                    }
                    else -> null
                }
            }

            is Cover.CoverUrl -> getRemoteImage(cover)?.decodeToImageBitmap()
        }

    suspend fun getAllUniqueCover(covers: List<Cover>): List<ByteArray> =
        covers.mapNotNull { getCoverByteArray(it) }.distinctBy { it.contentHashCode() }
}
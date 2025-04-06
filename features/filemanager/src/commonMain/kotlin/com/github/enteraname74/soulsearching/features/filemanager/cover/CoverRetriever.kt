package com.github.enteraname74.soulsearching.features.filemanager.cover

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Cover
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

class CoverRetriever(
    private val cachedCoverManager: CachedCoverManager,
    private val coverFileManager: CoverFileManager,
) {
    private suspend fun getCoverByteArray(cover: Cover): ByteArray? =
        when(cover) {
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

            is Cover.CoverUrl -> TODO("Implement CoverUrl support")
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

            is Cover.CoverUrl -> {
                // TODO: Implement logic for retrieving image
                null
            }
        }

    suspend fun getAllUniqueCover(covers: List<Cover>): List<ByteArray> =
        covers.mapNotNull { getCoverByteArray(it) }.distinctBy { it.contentHashCode() }
}
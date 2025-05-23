package com.github.enteraname74.soulsearching.features.filemanager.cover

import com.github.enteraname74.domain.model.Cover

class CoverRetriever(
    private val cachedCoverManager: CachedCoverManager,
    private val coverFileManager: CoverFileManager,
) {
    private suspend fun getCoverByteArray(cover: Cover): ByteArray? =
        when(cover) {
            is Cover.CoverFile -> {
                when {
                    cover.fileCoverId != null -> {
                        coverFileManager.getCoverData(
                            coverId = cover.fileCoverId!!,
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
            else -> null
        }

    suspend fun getAllUniqueCover(covers: List<Cover>): List<ByteArray> =
        covers.mapNotNull { getCoverByteArray(it) }.distinctBy { it.contentHashCode() }
}
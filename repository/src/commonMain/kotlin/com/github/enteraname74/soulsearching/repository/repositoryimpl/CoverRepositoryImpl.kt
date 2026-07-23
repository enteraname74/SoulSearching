package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.CoverRepository
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.repository.datasource.cover.CoverLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.cover.CoverRemoteDataSource
import kotlin.uuid.Uuid

/**
 * Repository of an ImageCover.
 */
class CoverRepositoryImpl(
    private val coverFileManager: CoverFileManager,
    private val cachedCoverManager: CachedCoverManager,
    private val coverLocalDataSource: CoverLocalDataSource,
    private val coverRemoteDataSource: CoverRemoteDataSource,
): CoverRepository {
    override suspend fun upsert(id: Uuid, data: ByteArray) {
        coverFileManager.saveCover(
            id = id,
            data = data,
        )
    }

    override suspend fun getAllCoverIds(): List<Uuid> =
        coverFileManager.getAllCoverIds()

    override suspend fun delete(coverId: Uuid) {
        coverFileManager.deleteFromId(
            id = coverId,
        )
    }

    override suspend fun isCoverUsed(coverId: Uuid): Boolean =
        coverLocalDataSource.isCoverUsed(coverId)

    override suspend fun getCoverImageBitmap(cover: Cover): ImageBitmap? =
        when(cover) {
            is Cover.CoverFile -> {
                when {
                    cover.fileCoverId != null -> {
                        coverFileManager.getCoverData(
                            coverId = cover.fileCoverId!!,
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
            is Cover.Url -> coverRemoteDataSource.getRemoteCover(cover)?.decodeToImageBitmap()
        }

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
            is Cover.Url -> coverRemoteDataSource.getRemoteCover(cover)
        }

    override suspend fun getAllUniqueCover(covers: List<Cover>): List<ByteArray> =
        covers.mapNotNull { getCoverByteArray(it) }.distinctBy { it.contentHashCode() }
}

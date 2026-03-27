package com.github.enteraname74.domain.repository

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Cover
import java.util.UUID

interface CoverRepository {
    suspend fun upsert(id: UUID, data: ByteArray)

    suspend fun getAllCoverIds(): List<UUID>

    suspend fun delete(coverId: UUID)

    suspend fun isCoverUsed(coverId: UUID): Boolean

    suspend fun getCoverImageBitmap(cover: Cover): ImageBitmap?

    suspend fun getAllUniqueCover(covers: List<Cover>): List<ByteArray>
}
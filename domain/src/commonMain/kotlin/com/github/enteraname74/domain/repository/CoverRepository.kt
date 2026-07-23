package com.github.enteraname74.domain.repository

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Cover
import kotlin.uuid.Uuid

interface CoverRepository {
    suspend fun upsert(id: Uuid, data: ByteArray)

    suspend fun getAllCoverIds(): List<Uuid>

    suspend fun delete(coverId: Uuid)

    suspend fun isCoverUsed(coverId: Uuid): Boolean

    suspend fun getCoverImageBitmap(cover: Cover): ImageBitmap?

    suspend fun getAllUniqueCover(covers: List<Cover>): List<ByteArray>
}

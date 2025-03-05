package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Cover
import java.util.*

interface CoverRepository {
    suspend fun upsert(id: UUID, data: ByteArray)

    suspend fun delete(cover: Cover)

    suspend fun delete(coverId: UUID)
}
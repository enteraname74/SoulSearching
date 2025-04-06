package com.github.enteraname74.domain.repository

import java.util.*

interface CoverRepository {
    suspend fun upsert(id: UUID, data: ByteArray)

    suspend fun delete(coverId: UUID)
}
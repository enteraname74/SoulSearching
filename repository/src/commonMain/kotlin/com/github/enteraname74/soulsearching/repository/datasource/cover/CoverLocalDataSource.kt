package com.github.enteraname74.soulsearching.repository.datasource.cover

import kotlin.uuid.Uuid

interface CoverLocalDataSource {
    suspend fun isCoverUsed(coverId: Uuid): Boolean
}

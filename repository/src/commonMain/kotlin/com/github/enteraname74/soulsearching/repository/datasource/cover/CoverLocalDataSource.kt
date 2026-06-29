package com.github.enteraname74.soulsearching.repository.datasource.cover

import java.util.UUID

interface CoverLocalDataSource {
    suspend fun isCoverUsed(coverId: UUID): Boolean
}
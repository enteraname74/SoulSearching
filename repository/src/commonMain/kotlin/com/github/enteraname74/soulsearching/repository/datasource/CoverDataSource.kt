package com.github.enteraname74.soulsearching.repository.datasource

import java.util.UUID

interface CoverDataSource {
    suspend fun isCoverUsed(coverId: UUID): Boolean
}
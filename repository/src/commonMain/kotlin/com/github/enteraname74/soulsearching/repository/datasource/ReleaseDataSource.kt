package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.Release

interface ReleaseDataSource {
    suspend fun getLatestRelease(): Release?
}
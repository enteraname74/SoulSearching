package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Release

interface ReleaseRepository {
    /**
     * Retrieves the information of the latest release of the application.
     * If nothing was found, returns null.
     */
    suspend fun getLatestRelease(): Release?
}
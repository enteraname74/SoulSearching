package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Release
import kotlinx.coroutines.flow.Flow

interface ReleaseRepository {
    /**
     * Retrieves the information of the latest release of the application.
     * If nothing was found, returns null.
     */
    fun getLatestRelease(): Flow<Release?>

    /**
     * Retrieve the latest release tag that the user has seen.
     */
    fun getLatestViewedReleaseTag(): Flow<String?>

    suspend fun fetchLatestRelease()

    /**
     * Delete the saved latest release
     */
    suspend fun deleteLatestRelease()

}
package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.*
import java.util.UUID

class SyncDataWithCloudUseCase(
    private val cloudRepository: CloudRepository,
) {

    /**
     * Sync artists, albums, songs and playlist with the cloud.
     * If all elements were fetched successfully, the last update at date is updated.
     */
    suspend operator fun invoke(): SoulResult<List<UUID>> =
        cloudRepository.syncDataWithCloud()

}
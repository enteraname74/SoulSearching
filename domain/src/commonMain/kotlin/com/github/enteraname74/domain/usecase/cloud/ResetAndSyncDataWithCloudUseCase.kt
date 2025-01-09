package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository
import java.util.UUID

class ResetAndSyncDataWithCloudUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(): SoulResult<List<UUID>> {
        // We first delete all the info cloud from the database
        musicRepository.deleteAll(DataMode.Cloud)

        // We then fetch the cloud data
        return musicRepository.syncWithCloud()
    }
}
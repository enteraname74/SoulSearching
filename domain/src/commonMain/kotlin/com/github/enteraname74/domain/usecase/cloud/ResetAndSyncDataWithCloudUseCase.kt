package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.repository.MusicRepository

class ResetAndSyncDataWithCloudUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke() {
        // We first delete all the info cloud from the database
        musicRepository.deleteAll(DataMode.Cloud)

        // We then fetch the cloud data
        musicRepository.syncWithCloud()
    }
}
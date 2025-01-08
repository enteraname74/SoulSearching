package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.repository.MusicRepository

class SyncDataWithCloudUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke() {
        musicRepository.syncWithCloud()
    }
}
package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository
import java.util.UUID

class SyncDataWithCloudUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(): SoulResult<List<UUID>> =
        musicRepository.syncWithCloud()
}
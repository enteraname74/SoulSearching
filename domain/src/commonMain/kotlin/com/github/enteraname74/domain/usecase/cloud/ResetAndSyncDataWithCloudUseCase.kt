package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.model.SoulResult
import java.util.*

class ResetAndSyncDataWithCloudUseCase(
    private val deleteCloudDataUseCase: DeleteCloudDataUseCase,
    private val syncDataWithCloudUseCase: SyncDataWithCloudUseCase
) {
    suspend operator fun invoke(): SoulResult<List<UUID>> {
        deleteCloudDataUseCase()
        return syncDataWithCloudUseCase()
    }
}
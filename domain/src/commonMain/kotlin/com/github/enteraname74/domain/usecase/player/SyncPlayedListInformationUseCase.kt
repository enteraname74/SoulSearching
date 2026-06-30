package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlayerRepository

class SyncPlayedListInformationUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(): SoulResult<Unit> = SoulResult.runCatching {
        playerRepository.syncSharedPlayedList()
        // For UI updates
        playerRepository.fetchUserListWhereIsIn().throwIfError()
    }
}
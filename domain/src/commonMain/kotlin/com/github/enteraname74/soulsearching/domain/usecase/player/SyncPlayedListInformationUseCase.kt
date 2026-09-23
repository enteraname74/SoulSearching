package com.github.enteraname74.soulsearching.domain.usecase.player

import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.repository.PlayerRepository

class SyncPlayedListInformationUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(): SoulResult<Unit> = SoulResult.runCatching {
        playerRepository.syncSharedPlayedList()
        // For UI updates
        playerRepository.fetchUserListWhereIsIn().throwIfError()
    }
}

package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlayerRepository
import java.util.UUID

class AddMusicsToSharedPlayedListUseCase(
    private val playerRepository: PlayerRepository,
    private val syncMusicForPlayerIfNeededUseCase: SyncMusicForPlayerIfNeededUseCase,
    private val syncPlayedListMusicsUseCase: SyncPlayedListMusicsUseCase,
) {
    suspend operator fun invoke(musicIds: List<UUID>): SoulResult<Unit> = SoulResult.runCatching {
        val musicRemoteIds: List<String> = syncMusicForPlayerIfNeededUseCase(musicIds)
            .mapNotNull { it.remoteId }

        playerRepository.addToSharedPlaylist(musicRemoteIds)
        syncPlayedListMusicsUseCase().throwIfError()
    }
}
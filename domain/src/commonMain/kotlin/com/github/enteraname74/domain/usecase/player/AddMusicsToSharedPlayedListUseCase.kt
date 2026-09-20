package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlayerRepository
import kotlin.uuid.Uuid

class AddMusicsToSharedPlayedListUseCase(
    private val playerRepository: PlayerRepository,
    private val syncMusicForPlayerIfNeededUseCase: SyncMusicForPlayerIfNeededUseCase,
    private val syncPlayedListMusicsUseCase: SyncPlayedListMusicsUseCase,
) {
    suspend fun local(musicIds: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        val musicRemoteIds: List<String> = syncMusicForPlayerIfNeededUseCase(musicIds)
            .mapNotNull { it.remoteId }

        playerRepository.addToSharedPlayedList(musicRemoteIds)
        syncPlayedListMusicsUseCase().throwIfError()
    }
}

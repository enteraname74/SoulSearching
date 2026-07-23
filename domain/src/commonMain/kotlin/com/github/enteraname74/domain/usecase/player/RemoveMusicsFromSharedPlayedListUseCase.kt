package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class RemoveMusicsFromSharedPlayedListUseCase(
    private val playerRepository: PlayerRepository,
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(musicIds: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        val remoteIds = musicRepository
            .getFromIds(musicIds)
            .firstOrNull()
            ?.mapNotNull { it.remoteId } ?: return@runCatching

        playerRepository.removeFromSharedPlayedList(musicRemoteIds = remoteIds)
    }
}

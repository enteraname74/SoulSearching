package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class SyncPlayedListMusicsUseCase(
    private val fetchPlayedListMusicsUseCase: FetchPlayedListMusicsUseCase,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(): SoulResult<Unit> = SoulResult.runCatching {
        val playedList = playerRepository
            .getCurrentPlayedList()
            .firstOrNull()
            ?.takeIf { it.scope.isRemote } ?: return@runCatching

        val playedListId = playedList.id
        val toDelete: List<Uuid> = playerRepository.getDeletedRemoteMusicIds(
            playedListId = playedListId,
        )
        val data = fetchPlayedListMusicsUseCase(
            playedListId = playedListId,
        )
        playerRepository.updatesMusics(
            musicIdsToRemove = toDelete,
            playerMusicsToAdd = data.first,
        )
        playerRepository.setPlayerMusicUsers(data.second)
    }
}

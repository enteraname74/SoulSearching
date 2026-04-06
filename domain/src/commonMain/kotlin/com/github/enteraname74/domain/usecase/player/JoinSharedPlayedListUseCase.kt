package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.repository.PlayerRepository

class JoinSharedPlayedListUseCase(
    private val fetchPlayedListMusicsUseCase: FetchPlayedListMusicsUseCase,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(code: String): SoulResult<Unit> = SoulResult.runCatching {
        val sharedPlayedList: SharedPlayedList = playerRepository.joinSharedList(
            code = code,
        )

        val playerMusics: List<PlayerMusic> = fetchPlayedListMusicsUseCase(
            playedListId = sharedPlayedList.id,
        )

        playerRepository.setupFromShared(
            sharedPlayedList = sharedPlayedList,
            playerMusics = playerMusics,
        )
    }
}
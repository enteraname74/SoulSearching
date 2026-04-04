package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.repository.PlayerRepository
import java.util.UUID

/**
 * TODOS
 * - ensure given ids already on cloud and upload if not (separate use case)
 * - create shared played list
 * - fetch musics (paginated, check if already downloaded, local first logic, scoped -> separate use case)
 * - save locally
 */
class CreateSharedPlayedListUseCase(
    private val syncMusicForPlayerIfNeededUseCase: SyncMusicForPlayerIfNeededUseCase,
    private val fetchPlayedListMusicsUseCase: FetchPlayedListMusicsUseCase,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(musicIds: List<UUID>): SoulResult<Unit> = SoulResult.runCatching {
        println("CLUELESS -- HERE TRES")
        val musicRemoteIds: List<String> = syncMusicForPlayerIfNeededUseCase(musicIds)
            .mapNotNull { it.remoteId }

        println("CLUELESS -- HERE QUATRO")
        val sharedPlayedList: SharedPlayedList = playerRepository.createSharedPlayedList(
            musicRemoteIds = musicRemoteIds,
        )
        println("CLUELESS -- HERE DOS")
        val playerMusics: List<PlayerMusic> = fetchPlayedListMusicsUseCase(
            playedListId = sharedPlayedList.id,
        )
        println("CLUELESS -- HERE CINQUO")
        playerRepository.setupFromShared(
            sharedPlayedList = sharedPlayedList,
            playerMusics = playerMusics,
        )
    }
}
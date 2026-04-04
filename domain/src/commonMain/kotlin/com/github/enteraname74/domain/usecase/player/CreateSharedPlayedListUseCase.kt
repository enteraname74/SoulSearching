package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.usecase.music.SyncMusicWithCloudUseCase
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * TODOS
 * - ensure given ids already on cloud and upload if not (separate use case)
 * - create shared played list
 * - fetch musics (paginated, check if already downloaded, local first logic, scoped -> separate use case)
 * - save locally
 */
class CreateSharedPlayedListUseCase(
    private val syncMusicWithCloudUseCase: SyncMusicWithCloudUseCase,
    private val fetchPlayedListMusicsUseCase: FetchPlayedListMusicsUseCase,
    private val playerRepository: PlayerRepository,
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(musicIds: List<UUID>): SoulResult<Unit> = SoulResult.runCatching {
        syncMusicWithCloudUseCase().throwIfError()

        val musicRemoteIds: List<String> = musicRepository.getFromIds(musicIds)
            .first()
            .mapNotNull { it.remoteId }

        val sharedPlayedList: SharedPlayedList = playerRepository.createSharedPlayedList(
            musicRemoteIds = musicRemoteIds,
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
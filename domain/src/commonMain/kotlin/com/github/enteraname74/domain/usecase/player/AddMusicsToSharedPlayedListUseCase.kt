package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import kotlin.uuid.Uuid

class AddMusicsToSharedPlayedListUseCase(
    private val playerRepository: PlayerRepository,
    private val syncMusicForPlayerIfNeededUseCase: SyncMusicForPlayerIfNeededUseCase,
    private val syncPlayedListMusicsUseCase: SyncPlayedListMusicsUseCase,
    private val cloudBackgroundSyncJob: CloudBackgroundSyncJob,
) {
    suspend fun local(musicIds: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        val musicRemoteIds: List<String> = syncMusicForPlayerIfNeededUseCase(musicIds)
            .mapNotNull { it.remoteId }

        playerRepository.addToSharedPlayedList(musicRemoteIds)
        syncPlayedListMusicsUseCase().throwIfError()
    }

    suspend fun url(url: String): SoulResult<Unit> = SoulResult.runCatching {
        // TODO SHARED PLAYED LIST: Returns the added music from the backend to avoid background sync?
        playerRepository.addMusicFromURL(url)
        syncPlayedListMusicsUseCase().throwIfError()
        // When adding a song from an URL, it will be added to our library, so we launch a backend sync.
        cloudBackgroundSyncJob.launchIfPossible()
    }
}

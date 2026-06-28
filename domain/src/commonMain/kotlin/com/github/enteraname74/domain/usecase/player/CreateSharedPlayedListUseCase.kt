package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.PlayerRepository
import java.util.*

class CreateSharedPlayedListUseCase(
    private val syncMusicForPlayerIfNeededUseCase: SyncMusicForPlayerIfNeededUseCase,
    private val fetchPlayedListMusicsUseCase: FetchPlayedListMusicsUseCase,
    private val playerRepository: PlayerRepository,
    private val settings: SoulSearchingSettings,
    private val registerSharedPlayedListEventsListenerUseCase: RegisterSharedPlayedListEventsListenerUseCase,
) {
    suspend operator fun invoke(musicIds: List<UUID>): SoulResult<Unit> = SoulResult.runCatching {
        val musicRemoteIds: List<String> = syncMusicForPlayerIfNeededUseCase(musicIds)
            .mapNotNull { it.remoteId }

        val sharedPlayedList: SharedPlayedList = playerRepository.createSharedPlayedList(
            musicRemoteIds = musicRemoteIds,
        )

        // We will reset the update timestamp to ensure that we fetch all elements:
        settings.set(
            key = SoulSearchingSettingsKeys.Player.SHARED_PLAYED_LIST_MUSIC_UPDATE_MILLIS.key,
            value = 0L,
        )

        val data = fetchPlayedListMusicsUseCase(
            playedListId = sharedPlayedList.id,
        )

        playerRepository.setupFromShared(
            sharedPlayedList = sharedPlayedList,
            playerMusics = data.first,
        )
        playerRepository.setPlayerMusicUsers(data.second)
        registerSharedPlayedListEventsListenerUseCase(
            listId = sharedPlayedList.id,
        )
    }
}
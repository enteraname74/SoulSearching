package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.repository.SharedPlayedListListener
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class RegisterSharedPlayedListEventsListenerUseCase(
    private val playerRepository: PlayerRepository,
    private val syncPlayedListInformationUseCase: SyncPlayedListInformationUseCase,
    private val syncPlayedListMusicsUseCase: SyncPlayedListMusicsUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
) {
    suspend operator fun invoke(listId: Uuid) {
        playerRepository.registerSharedPlayedListEventsListener(
            listener = object : SharedPlayedListListener {
                override suspend fun onClose() {
                    playerRepository.deletePlayedList(listId.toJavaUuid())
                    deleteMusicUseCase.deleteSharedMusics()
                }

                override suspend fun onSyncPlayedList() {
                    syncPlayedListInformationUseCase()
                }

                override suspend fun onSyncMusics() {
                    syncPlayedListMusicsUseCase()
                }
            }
        )
    }
}
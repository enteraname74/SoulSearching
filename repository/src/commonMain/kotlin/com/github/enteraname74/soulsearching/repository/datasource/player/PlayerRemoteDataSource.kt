package com.github.enteraname74.soulsearching.repository.datasource.player

import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.model.player.SharedPlayerMusic
import kotlin.uuid.Uuid

interface PlayerRemoteDataSource {
    suspend fun create(
        deviceId: String,
        musicIds: List<String>,
    ): SharedPlayedList

    suspend fun getDeletedMusicIds(
        deviceId: String,
        listId: Uuid,
        musicIdsToCheck: List<String>
    ): List<String>

    suspend fun getPlayedList(
        deviceId: String,
        listId: Uuid,
    ): SharedPlayedList

    suspend fun getPlayedListMusics(
        deviceId: String,
        listId: Uuid,
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?,
    ): List<SharedPlayerMusic>

    suspend fun deletePlayedList(
        deviceId: String,
        listId: Uuid,
    )

    suspend fun removeUserFromPlayedList(
        deviceId: String,
        listId: Uuid,
        userId: Uuid,
        deviceIdToRemove: String,
        userIdToRemove: Uuid,
    )

    suspend fun addMusics(
        deviceId: String,
        listId: Uuid,
        musicIds: List<String>,
    )

    suspend fun removeMusics(
        deviceId: String,
        listId: Uuid,
        musicIds: List<String>,
    )

    suspend fun updateState(
        deviceId: String,
        listId: Uuid,
        state: PlayedListState,
    ) : SharedPlayedList

    suspend fun updateCurrentMusic(
        deviceId: String,
        listId: Uuid,
        musicRemoteId: String,
    )

    suspend fun joinSharedList(
        deviceId: String,
        code: String,
    ): SharedPlayedList
}
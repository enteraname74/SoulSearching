package com.github.enteraname74.domain.repository

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.*
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface PlayerRepository {
    fun getAllPaginated(): Flow<PagingData<Music>>
    fun getAll(): Flow<List<Music>>
    fun getSize(): Flow<Int>
    fun getCurrentMusic(): Flow<PlayerMusic?>
    fun getCurrentState(): Flow<PlayedListState?>
    fun getNextMusic(): Flow<PlayerMusic?>
    fun getPreviousMusic(): Flow<PlayerMusic?>
    fun getCurrentMode(): Flow<PlayerMode?>
    fun getCurrentPlayedList(): Flow<PlayerPlayedList?>
    fun getCachedPlayedList(playlistId: String): Flow<PlayedListToContinue?>
    fun getCurrentPosition(): Flow<Int?>
    fun getCurrentProgress(): Flow<Int>
    fun getCurrentScope(): Flow<PlayedListScope?>

    suspend fun isAdminOfPlayedList(): Boolean

    suspend fun deleteAll(musicIds: List<Uuid>)

    suspend fun deleteCurrentPlayedList()

    suspend fun deletePlayedList(playedListId: Uuid)

    suspend fun deleteSharedListAndSync(listId: Uuid): SoulResult<Unit>

    /**
     * Setups a played list.
     * Returns true if the played list was set up, false if the setup was skipped.
     */
    suspend fun setup(playedListSetup: PlayedListSetup): Boolean

    suspend fun setupFromShared(
        sharedPlayedList: SharedPlayedList,
        playerMusics: List<PlayerMusic>,
    )

    suspend fun moveMusic(
        fromMusicId: Uuid,
        afterMusicId: Uuid
    )

    suspend fun setCurrent(
        musicId: Uuid,
    )

    suspend fun setProgress(progress: Int)

    suspend fun playNext()
    suspend fun playPrevious()

    suspend fun addAll(
        musics: List<Music>,
        mode: AddMusicMode,
    )

    suspend fun updatesMusics(
        musicIdsToRemove: List<Uuid>,
        playerMusicsToAdd: List<PlayerMusic>,
    )

    suspend fun continuePlayedList(
        playedListId: Uuid,
    )

    suspend fun switchPlayerMode()

    suspend fun removeCurrentAndPlayNext()

    suspend fun setPlayedListState(playedListState: PlayedListState)

    suspend fun togglePlayPause()

    suspend fun createSharedPlayedList(
        musicRemoteIds: List<String>
    ): SharedPlayedList

    suspend fun fetchPlayedListMusics(
        playedListId: Uuid
    ): List<SharedPlayerMusic>

    suspend fun getDeletedRemoteMusicIds(
        playedListId: Uuid,
    ): List<Uuid>

    suspend fun addToSharedPlayedList(
        musicRemoteIds: List<String>
    )

    suspend fun removeFromSharedPlayedList(
        musicRemoteIds: List<String>
    )

    suspend fun updateCurrentRemoteMusic(musicRemoteId: String)

    suspend fun syncSharedPlayedList()

    suspend fun joinSharedList(
        code: String,
    ): SharedPlayedList

    suspend fun removeUser(
        userId: Uuid,
        deviceId: String,
    )

    fun observeCurrentSharedUsers(): Flow<List<SharedPlayedListUser>>

    suspend fun registerSharedPlayedListEventsListener(
        listener: SharedPlayedListListener
    )

    suspend fun removeSharedPlayedListEventsListener()

    suspend fun setPlayerMusicUsers(playerMusicUsers: List<PlayerMusicUser>)

    /**
     * Observe the current list FullPlayerMusicUser if the list is a shared one (remote)
     */
    fun observeFullPlayerMusicUsers(): Flow<List<FullPlayerMusicUser>>

    suspend fun getDeviceId(): String

    suspend fun fetchUserListWhereIsIn(): SoulResult<Unit>

    fun observeAllSharedPlayedListPreview(): Flow<List<SharedPlayedListPreview>>

    suspend fun deleteAllSharedPlayedListPreviews()

    suspend fun getPlayerToken(): SoulResult<PlayerToken>
}

interface SharedPlayedListListener {
    suspend fun onConnected()
    suspend fun onClose()
    suspend fun onSyncPlayedList()
    suspend fun onSyncMusics()
}

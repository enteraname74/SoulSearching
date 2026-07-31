package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.AddMusicMode
import com.github.enteraname74.domain.model.player.FullPlayerMusicUser
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayedListSetup
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayedListToContinue
import com.github.enteraname74.domain.model.player.PlayedListType
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerMusicUser
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import com.github.enteraname74.domain.model.player.PlayerToken
import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.model.player.SharedPlayedListPreview
import com.github.enteraname74.domain.model.player.SharedPlayedListUser
import com.github.enteraname74.domain.model.player.SharedPlayerMusic
import com.github.enteraname74.domain.model.player.toPreview
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.repository.SharedPlayedListListener
import com.github.enteraname74.soulsearching.repository.datasource.DeviceLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid
import kotlin.coroutines.CoroutineContext

/**
 * Repository of a PlayerMusic.
 */
// TODO SHARED PLAYED LIST: Use LoadingManager on all calls that uses setPlayedListState
class PlayerRepositoryImpl(
    private val playerLocalDataSource: PlayerLocalDataSource,
    private val playerRemoteDataSource: PlayerRemoteDataSource,
    private val deviceLocalDataSource: DeviceLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val musicLocalDataSource: MusicLocalDataSource,
    private val settings: SoulSearchingSettings,
    private val workScope: CoroutineContext,
) : PlayerRepository {
    override fun getAllPaginated(): Flow<PagingData<Music>> =
        playerLocalDataSource.getAllPaginated()

    override fun getAll(): Flow<List<Music>> =
        playerLocalDataSource.getAll()

    override fun getSize(): Flow<Int> =
        playerLocalDataSource.getSize()

    override fun getCurrentMusic(): Flow<PlayerMusic?> =
        playerLocalDataSource.getCurrentMusic()

    override fun getCurrentState(): Flow<PlayedListState?> =
        playerLocalDataSource.getCurrentState()

    override fun getNextMusic(): Flow<PlayerMusic?> =
        playerLocalDataSource.getNextMusic()

    override fun getPreviousMusic(): Flow<PlayerMusic?> =
        playerLocalDataSource.getPreviousMusic()

    override fun getCurrentMode(): Flow<PlayerMode?> =
        playerLocalDataSource.getCurrentMode()

    override fun getCurrentPlayedList(): Flow<PlayerPlayedList?> =
        playerLocalDataSource.getCurrentPlayedList()

    override fun getCachedPlayedList(playlistId: String): Flow<PlayedListToContinue?> =
        playerLocalDataSource.getCachedPlayedList(playlistId)

    override fun getCurrentPosition(): Flow<Int?> =
        playerLocalDataSource.getCurrentPosition()

    override fun getCurrentProgress(): Flow<Int> =
        playerLocalDataSource.getCurrentProgress()

    override fun getCurrentScope(): Flow<PlayedListScope?> =
        playerLocalDataSource.getCurrentScope()

    override suspend fun isAdminOfPlayedList(): Boolean =
        playerLocalDataSource.getCurrentScope().firstOrNull()?.isAdmin == true

    override suspend fun setProgress(progress: Int) {
        playerLocalDataSource.setProgress(progress)
    }

    override suspend fun deleteAll(musicIds: List<Uuid>) {
        withContext(workScope) {
            playerLocalDataSource.deleteAll(musicIds)
        }
    }

    override suspend fun deleteCurrentPlayedList() {
        withContext(workScope) {
            val playedList = playerLocalDataSource
                .getCurrentPlayedList()
                .firstOrNull() ?: return@withContext

            playerLocalDataSource.deleteCurrentPlayedList()

            launch {
                when (playedList.scope) {
                    PlayedListScope.LocalUser -> {
                        // no-op
                    }

                    PlayedListScope.SharedGuest, PlayedListScope.SharedHost -> {
                        val userId = userLocalDataSource.observeUser().firstOrNull()?.id ?: return@launch
                        val deviceId = deviceLocalDataSource.getDeviceId()

                        playerRemoteDataSource.removeUserFromPlayedList(
                            deviceId = deviceId,
                            listId = playedList.id,
                            userId = userId,
                            deviceIdToRemove = deviceId,
                            userIdToRemove = userId,
                        )
                    }
                }
            }
        }
    }

    override suspend fun deletePlayedList(playedListId: Uuid) {
        withContext(workScope) {
            playerLocalDataSource.deletePlayedList(playedListId)
        }
    }

    override suspend fun deleteSharedListAndSync(listId: Uuid): SoulResult<Unit> = SoulResult.runCatching {
        playerRemoteDataSource.deletePlayedList(
            deviceId = deviceLocalDataSource.getDeviceId(),
            listId = listId,
        )
        fetchUserListWhereIsIn().throwIfError()
    }

    override suspend fun continuePlayedList(playedListId: Uuid) {
        withContext(workScope) {
            quitSharedPlayedListIfNeeded()
            playerLocalDataSource.continuePlayedList(playedListId)
        }
    }

    private suspend fun quitSharedPlayedListIfNeeded() {
        withContext(workScope) {
            val currentPlayedList = playerLocalDataSource.getCurrentPlayedList().firstOrNull() ?: return@withContext

            if (currentPlayedList.scope.isRemote) {
                val user = userLocalDataSource.observeUser().firstOrNull() ?: return@withContext
                val deviceId = deviceLocalDataSource.getDeviceId()

                playerRemoteDataSource.removeUserFromPlayedList(
                    deviceId = deviceId,
                    listId = currentPlayedList.id,
                    userId = user.id,
                    deviceIdToRemove = deviceId,
                    userIdToRemove = user.id
                )
            }
        }
    }

    override suspend fun setup(playedListSetup: PlayedListSetup): Boolean =
        withContext(workScope) {
            if (shouldSkipSetup(playedListSetup)) {
                return@withContext false
            }
            quitSharedPlayedListIfNeeded()

            playerLocalDataSource.upsertPlayedList(
                playedList = playedListSetup.toPlayedList(),
                playerMusics = playedListSetup.toPlayerMusics(),
            )
            true
        }

    override suspend fun setupFromShared(
        sharedPlayedList: SharedPlayedList,
        playerMusics: List<PlayerMusic>
    ) {
        withContext(workScope) {
            val userId: Uuid =
                userLocalDataSource.observeUser().firstOrNull()?.id ?: return@withContext

            playerLocalDataSource.upsertPlayedList(
                playedList = sharedPlayedList.toPlayedList(userId),
                playerMusics = playerMusics,
            )
            playerLocalDataSource.setSharedUsers(sharedPlayedList.buildUsers())
        }
    }

    override suspend fun moveMusic(fromMusicId: Uuid, afterMusicId: Uuid) {
        withContext(workScope) {
            playerLocalDataSource.moveMusic(
                fromMusicId = fromMusicId,
                toMusicId = afterMusicId,
            )
        }
    }

    private suspend fun shouldSkipSetup(
        playedListSetup: PlayedListSetup,
    ): Boolean =
        withContext(workScope) {
            if (playedListSetup.musics.isEmpty()) return@withContext true
            if (playedListSetup.forceOverride) return@withContext false

            val currentPlayedList: PlayerPlayedList = playerLocalDataSource
                .getCurrentPlayedList()
                .firstOrNull() ?: return@withContext false

            val currentMusic: PlayerMusic = playerLocalDataSource
                .getCurrentMusic()
                .firstOrNull() ?: return@withContext false

            val isSameMusic = currentMusic.music.musicId == playedListSetup.selectedMusic?.musicId
            val isSamePlaylist =
                if (currentPlayedList.playlistId == null && playedListSetup.listId == null) {
                    currentPlayedList.isMainPlaylist == playedListSetup.isMain
                } else if (currentPlayedList.playlistId != null && playedListSetup.listId != null) {
                    currentPlayedList.playlistId == playedListSetup.listId
                } else {
                    false
                }

            isSameMusic && isSamePlaylist
        }

    override suspend fun updatesMusics(
        musicIdsToRemove: List<Uuid>,
        playerMusicsToAdd: List<PlayerMusic>
    ) {
        withContext(workScope) {
            playerLocalDataSource.updatesMusics(
                musicIdsToRemove = musicIdsToRemove,
                playerMusicsToAdd = playerMusicsToAdd,
            )
        }
    }

    override suspend fun addAll(
        musics: List<Music>,
        mode: AddMusicMode
    ) {
        if (musics.isEmpty()) return

        withContext(workScope) {
            val currentPlayedList: PlayerPlayedList? = playerLocalDataSource
                .getCurrentPlayedList()
                .firstOrNull()

            // If we are not in a list
            if (currentPlayedList == null) {
                setup(
                    playedListSetup = PlayedListSetup.fromSelection(
                        musics = musics,
                        playlistId = null,
                        isMain = false,
                        scope = PlayedListScope.LocalUser,
                        type = PlayedListType.Local,
                    )
                )
                return@withContext
            }

            val currentMusic: PlayerMusic = playerLocalDataSource
                .getCurrentMusic()
                .firstOrNull() ?: return@withContext

            val filteredList: List<Music> = musics.filter {
                it.musicId != currentMusic.music.musicId
            }

            val newOrders: List<Double> = when (mode) {
                AddMusicMode.Next -> {
                    val currentOrder: Double = playerLocalDataSource
                        .getCurrentMusic()
                        .firstOrNull()
                        ?.let(currentPlayedList::getOrder) ?: return@withContext
                    val nextOrder: Double = playerLocalDataSource
                        .getNextMusic()
                        .firstOrNull()
                        ?.let(currentPlayedList::getOrder) ?: return@withContext

                    // We are at the last music of the list
                    if (nextOrder <= currentOrder) {
                        evenlySpacedExclusive(
                            count = filteredList.size,
                            a = currentOrder,
                            b = currentOrder + filteredList.size + 1,
                        )
                    } else {
                        evenlySpacedExclusive(
                            count = filteredList.size,
                            a = currentOrder,
                            b = nextOrder,
                        )
                    }

                }

                AddMusicMode.Queue -> {
                    val lastOrder: Double = playerLocalDataSource
                        .getLastMusic()
                        .firstOrNull()
                        ?.let(currentPlayedList::getOrder) ?: return@withContext

                    evenlySpacedExclusive(
                        count = filteredList.size,
                        a = lastOrder,
                        b = lastOrder + filteredList.size + 1,
                    )
                }
            }

            playerLocalDataSource.upsertAllMusics(
                filteredList.mapIndexed { index, music ->
                    PlayerMusic(
                        music = music,
                        playedListId = currentPlayedList.id,
                        order = newOrders[index],
                        shuffledOrder = newOrders[index],
                        lastPlayedMillis = null,
                    )
                }
            )
            playerLocalDataSource.handleListChange(musicIdsToKeep = filteredList.map { it.musicId })
        }
    }

    private fun evenlySpacedExclusive(
        count: Int,
        a: Double,
        b: Double
    ): List<Double> {
        if (count == 0) return emptyList()

        val step = (b - a) / (count + 1)

        return List(count) { index ->
            a + step * (index + 1)
        }
    }

    override suspend fun switchPlayerMode() {
        withContext(workScope) {
            playerLocalDataSource.switchPlayerMode()
        }
    }

    override suspend fun removeCurrentAndPlayNext() {
        withContext(workScope) {
            playerLocalDataSource.removeCurrentAndPlayNext()
        }
    }

    override suspend fun setPlayedListState(playedListState: PlayedListState) {
        withContext(workScope) {
            playerLocalDataSource.setState(playedListState)
            val playedList = playerLocalDataSource.getCurrentPlayedList().firstOrNull() ?: return@withContext

            if (playedList.scope == PlayedListScope.SharedHost) {
                runCatching {
                    val updatedList = playerRemoteDataSource.updateState(
                        deviceId = deviceLocalDataSource.getDeviceId(),
                        listId = playedList.id,
                        state = playedListState,
                    )
                    // We will not override the local value if it was loading, except if the new state is playing.
                    if (playedListState != PlayedListState.Loading || updatedList.state == SharedPlayedList.State.Playing) {
                        playerLocalDataSource.setState(updatedList.state.toPlayedListState())
                    }
                }
            }
        }
    }

    override suspend fun setCurrent(musicId: Uuid) {
        withContext(workScope) {
            playerLocalDataSource.setCurrent(musicId = musicId)
        }
    }

    override suspend fun playNext() {
        withContext(workScope) {
            val nextId: Uuid = playerLocalDataSource
                .getNextMusic().firstOrNull()?.music?.musicId ?: return@withContext

            playerLocalDataSource.setCurrent(musicId = nextId)
            setPlayedListState(PlayedListState.Playing)
        }
    }

    override suspend fun playPrevious() {
        withContext(workScope) {
            val previousId: Uuid = playerLocalDataSource
                .getPreviousMusic().firstOrNull()?.music?.musicId ?: return@withContext
            playerLocalDataSource.setCurrent(musicId = previousId)
            setPlayedListState(PlayedListState.Playing)
        }
    }

    override suspend fun togglePlayPause() {
        withContext(workScope) {
            val state: PlayedListState = playerLocalDataSource
                .getCurrentPlayedList()
                .firstOrNull()
                ?.state ?: return@withContext

            when (state) {
                PlayedListState.Playing -> setPlayedListState(PlayedListState.Paused)
                PlayedListState.Paused, PlayedListState.Loading -> setPlayedListState(
                    PlayedListState.Playing
                )

                else -> {
                    // no-op
                }
            }
        }
    }

    override suspend fun createSharedPlayedList(musicRemoteIds: List<String>): SharedPlayedList =
        withContext(workScope) {
            playerRemoteDataSource.create(
                deviceId = deviceLocalDataSource.getDeviceId(),
                musicIds = musicRemoteIds,
            )
        }

    override suspend fun fetchPlayedListMusics(playedListId: Uuid): List<SharedPlayerMusic> =
        withContext(workScope) {
            var page = 0
            val deviceId = deviceLocalDataSource.getDeviceId()

            val fetchedMusics: MutableList<SharedPlayerMusic> = mutableListOf()
            while (true) {
                val fetchedData = playerRemoteDataSource.getPlayedListMusics(
                    lastUpdateAt = settings
                        .get(SoulSearchingSettingsKeys.Player.SHARED_PLAYED_LIST_MUSIC_UPDATE_MILLIS)
                        .takeIf { it > 0L },
                    maxPerPage = MAX_MUSICS_PER_PAGE,
                    page = page,
                    deviceId = deviceId,
                    listId = playedListId,
                )

                fetchedMusics += fetchedData
                if (fetchedData.size < MAX_MUSICS_PER_PAGE) break

                page += 1
            }

            fetchedMusics
        }

    override suspend fun getDeletedRemoteMusicIds(
        playedListId: Uuid,
    ): List<Uuid> =
        withContext(workScope) {
            val musicRemoteIds = playerLocalDataSource
                .getAll()
                .firstOrNull()
                ?.mapNotNull { it.remoteId } ?: return@withContext emptyList()

            val deletedRemoteIds: List<String> = playerRemoteDataSource.getDeletedMusicIds(
                deviceId = deviceLocalDataSource.getDeviceId(),
                listId = playedListId,
                musicIdsToCheck = musicRemoteIds,
            )

            musicLocalDataSource.getIdsFromRemoteIds(deletedRemoteIds)
        }

    override suspend fun addToSharedPlayedList(musicRemoteIds: List<String>) {
        withContext(workScope) {
            playerRemoteDataSource.addMusics(
                deviceId = deviceLocalDataSource.getDeviceId(),
                listId = playerLocalDataSource.getCurrentPlayedList().firstOrNull()?.id
                    ?: return@withContext,
                musicIds = musicRemoteIds,
            )
        }
    }

    override suspend fun removeFromSharedPlayedList(musicRemoteIds: List<String>) {
        withContext(workScope) {
            playerRemoteDataSource.removeMusics(
                deviceId = deviceLocalDataSource.getDeviceId(),
                listId = playerLocalDataSource.getCurrentPlayedList().firstOrNull()?.id
                    ?: return@withContext,
                musicIds = musicRemoteIds,
            )
        }
    }

    override suspend fun updateCurrentRemoteMusic(musicRemoteId: String) {
        withContext(workScope) {
            val playedList = playerLocalDataSource.getCurrentPlayedList().firstOrNull() ?: return@withContext
            playerRemoteDataSource.updateCurrentMusic(
                deviceId = deviceLocalDataSource.getDeviceId(),
                listId = playedList.id,
                musicRemoteId = musicRemoteId
            )
        }
    }

    override suspend fun syncSharedPlayedList() {
        runCatching {
            withContext(workScope) {
                val playedList = playerLocalDataSource.getCurrentPlayedList().firstOrNull() ?: return@withContext
                val userId = userLocalDataSource.observeUser().firstOrNull()?.id ?: return@withContext

                val sharedList = playerRemoteDataSource.getPlayedList(
                    deviceId = deviceLocalDataSource.getDeviceId(),
                    listId = playedList.id,
                )
                /*
                If the user is in loading state (app launch), we don't want to force a full screen of the player view.
                To mitigate this issue, we keep the loading state if the remote list is paused.
                Else, if the list is in playing mode, we will let the full screen happen.
                 */
                if (playedList.state != PlayedListState.Loading || sharedList.state == SharedPlayedList.State.Playing) {
                    playerLocalDataSource.setState(sharedList.state.toPlayedListState())
                }
                playerLocalDataSource.setScope(sharedList.scope(userId))
                playerLocalDataSource.setSharedUsers(sharedList.buildUsers())
            }
        }
    }

    override fun observeCurrentSharedUsers(): Flow<List<SharedPlayedListUser>> =
        playerLocalDataSource.observeCurrentSharedUsers()

    override suspend fun joinSharedList(
        code: String,
    ): SharedPlayedList =
        withContext(workScope) {
            playerRemoteDataSource.joinSharedList(
                deviceId = deviceLocalDataSource.getDeviceId(),
                code = code,
            )
        }

    override suspend fun removeUser(userId: Uuid, deviceId: String) {
        withContext(workScope) {
            playerRemoteDataSource.removeUserFromPlayedList(
                deviceId = deviceLocalDataSource.getDeviceId(),
                listId = playerLocalDataSource.getCurrentPlayedList().firstOrNull()?.id
                    ?: return@withContext,
                userId = userLocalDataSource.observeUser().firstOrNull()?.id ?: return@withContext,
                deviceIdToRemove = deviceId,
                userIdToRemove = userId,
            )
        }
    }

    override suspend fun registerSharedPlayedListEventsListener(
        listener: SharedPlayedListListener
    ) {
        withContext(workScope) {
            playerRemoteDataSource.registerSharedPlayedListEventsListener(
                listId = playerLocalDataSource.getCurrentPlayedList().firstOrNull()?.id
                    ?: return@withContext,
                userId = userLocalDataSource.observeUser().firstOrNull()?.id ?: return@withContext,
                deviceId = deviceLocalDataSource.getDeviceId(),
                listener = listener,
            )
        }
    }

    override suspend fun removeSharedPlayedListEventsListener() {
        withContext(workScope) {
            playerRemoteDataSource.removeSharedPlayedListEventsListener()
        }
    }

    override suspend fun setPlayerMusicUsers(playerMusicUsers: List<PlayerMusicUser>) {
        withContext(workScope) {
            playerLocalDataSource.setPlayerMusicUsers(playerMusicUsers)
        }
    }

    override fun observeFullPlayerMusicUsers(): Flow<List<FullPlayerMusicUser>> =
        playerLocalDataSource.observeFullPlayerMusicUsers()

    override suspend fun getDeviceId(): String =
        deviceLocalDataSource.getDeviceId()

    override suspend fun fetchUserListWhereIsIn(): SoulResult<Unit> = SoulResult.runCatching {
        val user = userLocalDataSource.observeUser().firstOrNull() ?: return@runCatching

        val lists = playerRemoteDataSource.getAllWhereUserIsIn()
        playerLocalDataSource.setSharedPlayedListPreviews(
            previews = lists.map {
                it.toPreview(
                    userId = user.id,
                )
            }
        )
    }

    override fun observeAllSharedPlayedListPreview(): Flow<List<SharedPlayedListPreview>> =
        playerLocalDataSource.observeAllSharedPlayedListPreviews()

    override suspend fun deleteAllSharedPlayedListPreviews() {
        playerLocalDataSource.deleteAllSharedPlayedListPreviews()
    }

    override suspend fun getPlayerToken(): SoulResult<PlayerToken> = SoulResult.runCatching {
        playerRemoteDataSource.getPlayerToken()
    }

    private companion object {
        const val MAX_MUSICS_PER_PAGE = 300
    }
}

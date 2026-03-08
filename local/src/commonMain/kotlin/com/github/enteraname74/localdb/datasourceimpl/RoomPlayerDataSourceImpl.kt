package com.github.enteraname74.localdb.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.useWriterConnection
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayedListToContinue
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.player.RoomCompletePlayerMusic
import com.github.enteraname74.localdb.model.player.RoomPlayerMusic
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicProgress
import com.github.enteraname74.localdb.model.player.toRoomPlayerMusic
import com.github.enteraname74.localdb.model.player.toRoomPlayerPlayedList
import com.github.enteraname74.localdb.utils.PagingUtils
import com.github.enteraname74.soulsearching.repository.datasource.PlayerDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
internal class RoomPlayerDataSourceImpl(
    private val appDatabase: AppDatabase
) : PlayerDataSource {
    private val playerMusicDao = appDatabase.playerMusicDao
    private val listDao = appDatabase.playerPlayedListDao
    private val progressDao = appDatabase.playerMusicProgressDao

    override fun getAllPaginated(): Flow<PagingData<Music>> =
        listDao.getCurrentMode().flatMapLatest { mode ->
            Pager(
                config = PagingConfig(
                    pageSize = PagingUtils.PAGE_SIZE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    when (mode) {
                        PlayerMode.Loop -> playerMusicDao.getCurrentMusicPaged()
                        else -> playerMusicDao.getAllPaginated()
                    }
                }
            ).flow.map { pagingData ->
                pagingData.map { it.toPlayerMusic().music }
            }
        }

    override fun getAll(): Flow<List<Music>> =
        listDao.getCurrentMode().flatMapLatest { mode ->
            when (mode) {
                PlayerMode.Loop -> playerMusicDao.getCurrentMusic().map {
                    listOfNotNull(
                        it?.completeMusic?.toMusic()
                    )
                }
                else -> playerMusicDao.getAllAsFlow().map { list ->
                    list.map { it.completeMusic.toMusic() }
                }
            }
        }

    override fun getSize(): Flow<Int> =
        playerMusicDao.getSize()

    override fun getCurrentMusic(): Flow<PlayerMusic?> {
        return playerMusicDao.getCurrentMusic().map {
            it?.toPlayerMusic()
        }
    }


    override fun getNextMusic(
        musicIdsToSkip: List<UUID>,
    ): Flow<PlayerMusic?> =
        withGlobalState { data ->
            when {
                data.mode == PlayerMode.Loop -> playerMusicDao.getCurrentMusic()
                data.currentOrder == null -> flowOf(null)
                data.nextIsFirst -> playerMusicDao.getFirst(musicIdsToSkip)
                else -> playerMusicDao.getNextMusic(
                    order = data.currentOrder,
                    musicIdsToSkip = musicIdsToSkip,
                )
            }
        }.map { it?.toPlayerMusic() }

    override fun getLastMusic(): Flow<PlayerMusic?> =
        listDao.getCurrentMode().flatMapLatest { mode ->
            if (mode == PlayerMode.Loop) {
                playerMusicDao.getCurrentMusic()
            } else {
                playerMusicDao.getLast()
            }
        }.map { it?.toPlayerMusic() }

    override fun getPreviousMusic(): Flow<PlayerMusic?> =
        withGlobalState { data ->
            when {
                data.mode == PlayerMode.Loop -> playerMusicDao.getCurrentMusic()
                data.currentOrder == null -> flowOf(null)
                data.previousIsLast -> playerMusicDao.getLast()
                else -> playerMusicDao.getPreviousMusic(data.currentOrder)
            }
        }.map { it?.toPlayerMusic() }

    override suspend fun upsertAllMusics(playerMusics: List<PlayerMusic>) {
        playerMusicDao.upsertAll(
            playerMusics = playerMusics.map { it.toRoomPlayerMusic() }
        )
    }

    override suspend fun moveMusic(fromMusicId: UUID, toMusicId: UUID) {
        val musicToMove: RoomPlayerMusic = playerMusicDao.getOfCurrentList(fromMusicId) ?: return
        val mode: PlayerMode = listDao.getCurrentMode().firstOrNull() ?: return

        val musicToMoveOrder: Double = musicToMove.usedOrder(mode)
        val toMusicOrder: Double = playerMusicDao.getOfCurrentList(toMusicId)?.usedOrder(mode) ?: return

        val isMovingUp = musicToMoveOrder > toMusicOrder

        val newAfterMusic: RoomPlayerMusic? = playerMusicDao.getNextMusic(
            order = toMusicOrder,
            musicIdsToSkip = emptyList()
        ).firstOrNull()?.playerMusic

        val newBeforeMusic: RoomPlayerMusic? = playerMusicDao.getPreviousMusic(
            order = toMusicOrder,
        ).firstOrNull()?.playerMusic

        val first: RoomPlayerMusic = playerMusicDao.getFirst(emptyList()).firstOrNull()?.playerMusic ?: return
        val last: RoomPlayerMusic = playerMusicDao.getLast().firstOrNull()?.playerMusic ?: return

        val isAtTopOfList = toMusicId == first.musicId || newBeforeMusic == null
        val isAtBottomOfList = newAfterMusic == null

        val newOrder = when {
            isAtTopOfList -> toMusicOrder - 1
            isAtBottomOfList -> last.usedOrder(mode) + 1
            isMovingUp -> (newBeforeMusic.usedOrder(mode) + toMusicOrder) / 2
            else -> (toMusicOrder + newAfterMusic.usedOrder(mode)) / 2
        }

        playerMusicDao.updateAll(
            playerMusics = listOf(
                musicToMove.updateUsedOrder(
                    mode = mode,
                    newOrder = newOrder,
                )
            )
        )
    }

    override suspend fun deleteAll(musicIds: List<UUID>) {
        val currentId = playerMusicDao.getCurrentMusic().firstOrNull()?.playerMusic?.musicId ?: return
        if (currentId in musicIds) {
            val next = getNextMusic().firstOrNull()?.music?.musicId ?: return
            // TODO PLAYER: Set in writer connection?
            setCurrent(next)
        }
        appDatabase.useWriterConnection {
            playerMusicDao.deleteAll(musicIds)
            val totalAfterDeletion = playerMusicDao.getSize().firstOrNull() ?: 0
            if (totalAfterDeletion == 0) {
                deleteCurrentPlayedList()
            }
        }
    }

    override fun getCurrentMode(): Flow<PlayerMode?> =
        listDao.getCurrentMode()

    override fun getCurrentState(): Flow<PlayedListState?> =
        listDao.getCurrentState()

    override fun getCurrentPlayedList(): Flow<PlayerPlayedList?> =
        listDao.getCurrentPlayedList().map {
            it?.toPlayerPlayedList()
        }

    override fun getCachedPlayedList(playlistId: String): Flow<PlayedListToContinue?> =
        listDao.getCachedPlayedList(playlistId).flatMapLatest { list ->
            if (list != null) {
                playerMusicDao.getCurrentOfPlayedList(list.id).map { music ->
                    if (music != null) {
                        PlayedListToContinue(
                            playedListId = list.id,
                            currentMusic = PlayedListToContinue.CurrentMusic(
                                name = music.completeMusic.music.name,
                                artists = music.completeMusic.toMusic().artistsNames,
                            )
                        )
                    } else {
                        null
                    }
                }
            } else {
                flowOf(null)
            }
        }.distinctUntilChanged()

    override fun getCurrentPosition(): Flow<Int?> =
        playerMusicDao.getCurrentMusic().flatMapLatest { currentMusic ->
            listDao.getCurrentMode().flatMapLatest { mode ->
                if (currentMusic == null) {
                    flowOf(null)
                } else if (mode == PlayerMode.Loop) {
                    flowOf(1)
                } else {
                    playerMusicDao.getPositionInList(currentMusic.playerMusic.musicId)
                }
            }
        }

    override suspend fun upsertPlayedList(
        playedList: PlayerPlayedList,
        playerMusics: List<PlayerMusic>,
    ) {
        appDatabase.useWriterConnection {
            // We delete the same played list if any
            playedList.playlistId?.let {
                listDao.deletePlaylist(playlistId = it)
            }
            // We will also delete the main list and/or search lists
            listDao.deleteMainAndSearch()

            // Hide all played list
            listDao.cacheAll()

            // Move focus to the new one
            listDao.upsert(playedList.toRoomPlayerPlayedList())
            playerMusicDao.upsertAll(
                playerMusics = playerMusics.map { it.toRoomPlayerMusic() }
            )
        }
    }

    override suspend fun deletePlayedList(playedListId: UUID) {
        listDao.delete(playedListId)
    }

    override suspend fun deleteCurrentPlayedList() {
        listDao.deleteCurrent()
    }

    override suspend fun handleListChange(musicIdsToKeep: List<UUID>) {
        appDatabase.useWriterConnection {
            val currentMode: PlayerMode = listDao
                .getCurrentMode()
                .firstOrNull() ?: return@useWriterConnection

            val currentMusicId: UUID = playerMusicDao
                .getCurrentMusic()
                .firstOrNull()
                ?.playerMusic?.musicId ?: return@useWriterConnection

            when (currentMode) {
                PlayerMode.Normal -> {
                    // no-op
                }

                PlayerMode.Shuffle -> {
                    /*
                    Before going back to Normal mode,
                    we will apply the shuffled order to the order of the musics.
                     */
                    playerMusicDao.switchToNormalWithShuffleOrder()
                }

                PlayerMode.Loop -> {
                    /*
                    Before going back to Normal mode,
                    we will clean all songs except the current one and the given list
                     */
                    playerMusicDao.deleteAllExpect(musicIdsToKeep + currentMusicId)
                }
            }
            listDao.setMode(PlayerMode.Normal)
        }
    }

    override suspend fun setState(state: PlayedListState) {
        listDao.setState(state)
    }

    override suspend fun continuePlayedList(playedListId: UUID) {
        appDatabase.useWriterConnection {
            listDao.deleteMainAndSearch()
            listDao.cacheAll()
            listDao.setStateOfId(
                state = PlayedListState.Playing,
                playedListId = playedListId,
            )
        }
    }

    private suspend fun shuffle() {
        listDao.setMode(PlayerMode.Shuffle)
        val current: RoomPlayerMusic = playerMusicDao
            .getCurrentMusic()
            .firstOrNull()
            ?.playerMusic
            ?.copy(shuffledOrder = 0.0) ?: return

        // All without the current music
        val allExceptCurrent = playerMusicDao
            .getAll()
            .takeIf { it.isNotEmpty() }
            ?.minus(current) ?: return

        // The current music will be placed in the first position
        val randomizedOrder: List<Double> = allExceptCurrent.indices
            .shuffled()
            .map {
                // We want to move one step away as we will add the current music later
                (it + 1).toDouble()
            }
        val shuffledList: List<RoomPlayerMusic> =
            allExceptCurrent.mapIndexed { index, playerMusic ->
                playerMusic.copy(
                    shuffledOrder = randomizedOrder[index]
                )
            }

        playerMusicDao.updateAll(
            // We re-add the current music
            playerMusics = shuffledList + current,
        )
    }

    override suspend fun setCurrent(musicId: UUID) {
        appDatabase.useWriterConnection {
            playerMusicDao.setCurrent(
                musicId = musicId,
                lastPlayedMillis = Clock.System.now().toEpochMilliseconds(),
            )
        }
    }

    override fun getCurrentProgress(): Flow<Int> =
        listDao.getCurrentPlayedList().flatMapLatest { list ->
            playerMusicDao.getCurrentMusic().flatMapLatest { completePlayerMusic ->
                if (list == null || completePlayerMusic == null) {
                    flowOf(0)
                } else {
                    progressDao.getCurrent(
                        listId = list.id,
                        playerMusicId = completePlayerMusic.playerMusic.id,
                    ).map { it?.progress ?: 0 }
                }
            }
        }

    override suspend fun setProgress(progress: Int) {
        val currentPlayerMusicId: String = playerMusicDao.getCurrentMusic().firstOrNull()?.playerMusic?.id ?: return
        val currentListId: UUID = listDao.getCurrentPlayedList().firstOrNull()?.id ?: return

        progressDao.upsert(
            progress = RoomPlayerMusicProgress(
                playedListId = currentListId,
                playerMusicId = currentPlayerMusicId,
                progress = progress,
            )
        )
    }

    override suspend fun switchPlayerMode() {
        appDatabase.useWriterConnection {
            val nextMode: PlayerMode = listDao
                .getCurrentMode()
                .firstOrNull()
                ?.next() ?: return@useWriterConnection

            when (nextMode) {
                PlayerMode.Normal, PlayerMode.Loop -> {
                    // Only need to move the mode
                    listDao.setMode(nextMode)
                }

                PlayerMode.Shuffle -> {
                    // Need to shuffle the musics
                    shuffle()
                }
            }
        }
    }

    override suspend fun removeCurrentAndPlayNext() {
        appDatabase.useWriterConnection {
            val currentId: UUID = playerMusicDao
                .getCurrentMusic()
                .firstOrNull()
                ?.playerMusic
                ?.musicId ?: return@useWriterConnection

            val nextId: UUID = getNextMusic()
                .firstOrNull()
                ?.music?.musicId ?: return@useWriterConnection

            playerMusicDao.delete(currentId)
            playerMusicDao.setCurrent(
                musicId = nextId,
                lastPlayedMillis = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    private fun <T> withGlobalState(transform: suspend (GlobalState) -> Flow<T>): Flow<T> =
        combine(
            playerMusicDao.getCurrentMusic(),
            playerMusicDao.getFirst(emptyList()),
            playerMusicDao.getLast(),
            listDao.getCurrentMode(),
        ) { current, first, last, mode ->
            GlobalState(
                current = current,
                first = first,
                last = last,
                mode = mode,
            )
        }.flatMapLatest(transform)

    data class GlobalState(
        val current: RoomCompletePlayerMusic?,
        val first: RoomCompletePlayerMusic?,
        val last: RoomCompletePlayerMusic?,
        val mode: PlayerMode?,
    ) {
        val nextIsFirst: Boolean = current == last
        val previousIsLast: Boolean = current == first
        val currentOrder: Double? = if (mode == PlayerMode.Shuffle) {
            current?.playerMusic?.shuffledOrder
        } else {
            current?.playerMusic?.order
        }
    }
}
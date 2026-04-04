package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.player.AddMusicMode
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayedListSetup
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayedListToContinue
import com.github.enteraname74.domain.model.player.PlayedListType
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.model.player.SharedPlayerMusic
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.soulsearching.repository.datasource.DeviceLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.CoroutineContext

/**
 * Repository of a PlayerMusic.
 */
class PlayerRepositoryImpl(
    private val playerLocalDataSource: PlayerLocalDataSource,
    private val playerRemoteDataSource: PlayerRemoteDataSource,
    private val deviceLocalDataSource: DeviceLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
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

    override suspend fun setProgress(progress: Int) {
        playerLocalDataSource.setProgress(progress)
    }

    override suspend fun deleteAll(musicIds: List<UUID>) {
        withContext(workScope) {
            playerLocalDataSource.deleteAll(musicIds)
        }
    }

    override suspend fun deleteCurrentPlayedList() {
        withContext(workScope) {
            playerLocalDataSource.deleteCurrentPlayedList()
        }
    }

    override suspend fun deletePlayedList(playedListId: UUID) {
        withContext(workScope) {
            playerLocalDataSource.deletePlayedList(playedListId)
        }
    }

    override suspend fun continuePlayedList(playedListId: UUID) {
        withContext(workScope) {
            playerLocalDataSource.continuePlayedList(playedListId)
        }
    }

    override suspend fun setup(playedListSetup: PlayedListSetup) {
        withContext(workScope) {
            if (shouldSkipSetup(playedListSetup)) return@withContext

            playerLocalDataSource.upsertPlayedList(
                playedList = playedListSetup.toPlayedList(),
                playerMusics = playedListSetup.toPlayerMusics(),
            )
        }
    }

    override suspend fun moveMusic(fromMusicId: UUID, afterMusicId: UUID) {
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
            if (playedListSetup.forceOverride) return@withContext false

            val currentPlayedList: PlayerPlayedList = playerLocalDataSource
                .getCurrentPlayedList()
                .firstOrNull() ?: return@withContext false

            val currentMusic: PlayerMusic = playerLocalDataSource
                .getCurrentMusic()
                .firstOrNull() ?: return@withContext false

            val isSameMusic = currentMusic.music.musicId == playedListSetup.selectedMusic?.musicId
            val isSamePlaylist = if (currentPlayedList.playlistId == null && playedListSetup.listId == null) {
                currentPlayedList.isMainPlaylist == playedListSetup.isMain
            } else if (currentPlayedList.playlistId != null && playedListSetup.listId != null) {
                currentPlayedList.playlistId == playedListSetup.listId
            } else {
                false
            }

            isSameMusic && isSamePlaylist
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
        }
    }

    override suspend fun setCurrent(musicId: UUID) {
        withContext(workScope) {
            playerLocalDataSource.setCurrent(musicId = musicId)
        }
    }

    override suspend fun playNext() {
        withContext(workScope) {
            val nextId: UUID = playerLocalDataSource
                .getNextMusic().firstOrNull()?.music?.musicId ?: return@withContext

            playerLocalDataSource.setCurrent(musicId = nextId)
            playerLocalDataSource.setState(PlayedListState.Playing)
        }
    }

    override suspend fun playPrevious() {
        withContext(workScope) {
            val previousId: UUID = playerLocalDataSource
                .getPreviousMusic().firstOrNull()?.music?.musicId ?: return@withContext
            playerLocalDataSource.setCurrent(musicId = previousId)
            playerLocalDataSource.setState(PlayedListState.Playing)
        }
    }

    override suspend fun togglePlayPause() {
        withContext(workScope) {
            val state: PlayedListState = playerLocalDataSource
                .getCurrentPlayedList()
                .firstOrNull()
                ?.state ?: return@withContext

            when (state) {
                PlayedListState.Playing -> playerLocalDataSource.setState(PlayedListState.Paused)
                PlayedListState.Paused, PlayedListState.Loading -> playerLocalDataSource.setState(PlayedListState.Playing)
                else -> {
                    // no-op
                }
            }
        }
    }

    override suspend fun createSharedPlayedList(musicIds: List<String>): SharedPlayedList =
        withContext(workScope) {
            playerRemoteDataSource.create(
                deviceId = deviceLocalDataSource.getDeviceId(),
                musicIds = musicIds,
            )
        }
}
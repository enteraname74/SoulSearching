package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.AddMusicMode
import com.github.enteraname74.domain.model.player.PlayedListSetup
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.soulsearching.repository.datasource.PlayerDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.CoroutineContext

/**
 * Repository of a PlayerMusic.
 */
class PlayerRepositoryImpl(
    private val playerDataSource: PlayerDataSource,
    private val workScope: CoroutineContext,
) : PlayerRepository {
    override fun getAllPaginated(): Flow<PagingData<Music>> =
        playerDataSource.getAllPaginated()

    override fun getSize(): Flow<Int> =
        playerDataSource.getSize()

    override fun getCurrentMusic(): Flow<PlayerMusic?> =
        playerDataSource.getCurrentMusic()

    override fun getCurrentState(): Flow<PlayedListState?> =
        playerDataSource.getCurrentState()

    override fun getNextMusic(): Flow<PlayerMusic?> =
        playerDataSource.getNextMusic()

    override fun getPreviousMusic(): Flow<PlayerMusic?> =
        playerDataSource.getPreviousMusic()

    override fun getCurrentMode(): Flow<PlayerMode?> =
        playerDataSource.getCurrentMode()

    override fun getCurrentPlayedList(): Flow<PlayerPlayedList?> =
        playerDataSource.getCurrentPlayedList()

    override fun getCurrentPosition(): Flow<Int?> =
        playerDataSource.getCurrentPosition()

    override fun getCurrentProgress(): Flow<Int> =
        playerDataSource.getCurrentProgress()

    override suspend fun setProgress(progress: Int) {
        playerDataSource.setProgress(progress)
    }

    override suspend fun deleteAll(musicIds: List<UUID>) {
        withContext(workScope) {
            playerDataSource.deleteAll(musicIds)
        }
    }

    override suspend fun deleteCurrentPlayedList() {
        withContext(workScope) {
            playerDataSource.deleteCurrentPlayedList()
        }
    }

    override suspend fun setup(playedListSetup: PlayedListSetup) {
        withContext(workScope) {
            if (shouldSkipSetup(playedListSetup)) return@withContext

            playerDataSource.upsertPlayedList(
                playedList = playedListSetup.toPlayedList(),
                playerMusics = playedListSetup.toPlayerMusics(),
            )
        }
    }

    private suspend fun shouldSkipSetup(
        playedListSetup: PlayedListSetup,
    ): Boolean =
        withContext(workScope) {
            if (playedListSetup.forceOverride) return@withContext false

            val currentPlayedList: PlayerPlayedList = playerDataSource
                .getCurrentPlayedList()
                .firstOrNull() ?: return@withContext false

            val currentMusic: PlayerMusic = playerDataSource
                .getCurrentMusic()
                .firstOrNull() ?: return@withContext false

            val isSameMusic = currentMusic.music.musicId == playedListSetup.selectedMusic?.musicId
            val isSamePlaylist = if (currentPlayedList.playlistId == null && playedListSetup.listId == null) {
                currentPlayedList.isMainPlaylist == playedListSetup.isMain
            } else if (currentPlayedList.playlistId != null && playedListSetup.listId != null) {
                currentPlayedList.playlistId!!.compareTo(playedListSetup.listId) == 0
            } else {
                false
            }

            isSameMusic && isSamePlaylist
        }

    override suspend fun add(
        music: Music,
        mode: AddMusicMode
    ) {
        withContext(workScope) {
            val currentPlayedList: PlayerPlayedList? = playerDataSource
                .getCurrentPlayedList()
                .firstOrNull()

            // If we are not in a list
            if (currentPlayedList == null) {
                setup(playedListSetup = PlayedListSetup.fromMusic(music))
                return@withContext
            }

            val currentMusic: PlayerMusic = playerDataSource
                .getCurrentMusic()
                .firstOrNull() ?: return@withContext

            if (currentMusic.music.musicId == music.musicId) return@withContext

            val newOrder: Double = when (mode) {
                AddMusicMode.Next -> {
                    val currentOrder: Double = playerDataSource
                        .getCurrentMusic()
                        .firstOrNull()
                        ?.let(currentPlayedList::getOrder) ?: return@withContext
                    val nextOrder: Double = playerDataSource
                        .getNextMusic()
                        .firstOrNull()
                        ?.let(currentPlayedList::getOrder) ?: return@withContext

                    // We are at the last music of the list or the next one is the first one
                    if (nextOrder <= currentOrder) {
                        currentOrder + 1
                    } else {
                        (currentOrder + nextOrder) / 2
                    }

                }

                AddMusicMode.Queue -> {
                    val lastOrder: Double = playerDataSource
                        .getLastMusic()
                        .firstOrNull()
                        ?.let(currentPlayedList::getOrder) ?: return@withContext

                    lastOrder + 1
                }
            }

            playerDataSource.upsertAllMusics(
                listOf(
                    PlayerMusic(
                        music = music,
                        playedListId = currentPlayedList.id,
                        order = newOrder,
                        shuffledOrder = newOrder,
                        lastPlayedMillis = null,
                    )
                )
            )
            playerDataSource.handleListChange(musicIdsToKeep = listOf(music.musicId))
        }
    }

    override suspend fun addAll(
        musics: List<Music>,
        mode: AddMusicMode
    ) {
        if (musics.isEmpty()) return

        withContext(workScope) {
            val currentPlayedList: PlayerPlayedList? = playerDataSource
                .getCurrentPlayedList()
                .firstOrNull()

            // If we are not in a list
            if (currentPlayedList == null) {
                setup(playedListSetup = PlayedListSetup.fromSelection(musics))
                return@withContext
            }

            val currentMusic: PlayerMusic = playerDataSource
                .getCurrentMusic()
                .firstOrNull() ?: return@withContext

            val filteredList: List<Music> = musics.filter {
                it.musicId != currentMusic.music.musicId
            }

            val newOrders: List<Double> = when (mode) {
                AddMusicMode.Next -> {
                    val currentOrder: Double = playerDataSource
                        .getCurrentMusic()
                        .firstOrNull()
                        ?.let(currentPlayedList::getOrder) ?: return@withContext
                    val nextOrder: Double = playerDataSource
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
                    val lastOrder: Double = playerDataSource
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

            playerDataSource.upsertAllMusics(
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
            playerDataSource.handleListChange(musicIdsToKeep = filteredList.map { it.musicId })
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
            playerDataSource.switchPlayerMode()
        }
    }

    override suspend fun removeCurrentAndPlayNext() {
        withContext(workScope) {
            playerDataSource.removeCurrentAndPlayNext()
        }
    }

    override suspend fun setPlayedListState(playedListState: PlayedListState) {
        withContext(workScope) {
            playerDataSource.setState(playedListState)
        }
    }

    override suspend fun setCurrent(musicId: UUID) {
        withContext(workScope) {
            playerDataSource.setCurrent(musicId = musicId)
        }
    }

    override suspend fun playNext() {
        withContext(workScope) {
            val nextId: UUID = playerDataSource
                .getNextMusic().firstOrNull()?.music?.musicId ?: return@withContext

            playerDataSource.setCurrent(musicId = nextId)
            playerDataSource.setState(PlayedListState.Playing)
        }
    }

    override suspend fun playPrevious() {
        withContext(workScope) {
            val previousId: UUID = playerDataSource
                .getPreviousMusic().firstOrNull()?.music?.musicId ?: return@withContext
            playerDataSource.setCurrent(musicId = previousId)
            playerDataSource.setState(PlayedListState.Playing)
        }
    }

    override suspend fun togglePlayPause() {
        withContext(workScope) {
            val state: PlayedListState = playerDataSource
                .getCurrentPlayedList()
                .firstOrNull()
                ?.state ?: return@withContext

            when (state) {
                PlayedListState.Playing -> playerDataSource.setState(PlayedListState.Paused)
                PlayedListState.Paused, PlayedListState.Loading -> playerDataSource.setState(PlayedListState.Playing)
                else -> {
                    // no-op
                }
            }
        }
    }
}
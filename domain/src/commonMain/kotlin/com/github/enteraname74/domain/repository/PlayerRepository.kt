package com.github.enteraname74.domain.repository

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.AddMusicMode
import com.github.enteraname74.domain.model.player.PlayedListSetup
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayedListToContinue
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import kotlinx.coroutines.flow.Flow
import java.util.UUID

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

    suspend fun deleteAll(musicIds: List<UUID>)

    suspend fun deleteCurrentPlayedList()

    suspend fun deletePlayedList(playedListId: UUID)

    suspend fun setup(playedListSetup: PlayedListSetup)

    suspend fun moveMusic(
        fromMusicId: UUID,
        afterMusicId: UUID
    )

    suspend fun add(
        music: Music,
        mode: AddMusicMode
    )

    suspend fun setCurrent(
        musicId: UUID,
    )
    suspend fun setProgress(progress: Int)

    suspend fun playNext()
    suspend fun playPrevious()

    suspend fun addAll(
        musics: List<Music>,
        mode: AddMusicMode,
    )

    suspend fun continuePlayedList(
        playedListId: UUID,
    )

    suspend fun switchPlayerMode()

    suspend fun removeCurrentAndPlayNext()

    suspend fun setPlayedListState(playedListState: PlayedListState)

    suspend fun togglePlayPause()
}
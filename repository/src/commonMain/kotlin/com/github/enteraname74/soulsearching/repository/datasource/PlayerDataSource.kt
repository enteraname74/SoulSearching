package com.github.enteraname74.soulsearching.repository.datasource
import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a PlayerMusic.
 */
interface PlayerDataSource {
    fun getAllPaginated(): Flow<PagingData<Music>>
    fun getSize(): Flow<Int>
    fun getCurrentMusic(): Flow<PlayerMusic?>
    fun getNextMusic(
        musicIdsToSkip: List<UUID> = emptyList()
    ): Flow<PlayerMusic?>
    fun getLastMusic(): Flow<PlayerMusic?>
    fun getPreviousMusic(): Flow<PlayerMusic?>

    fun getCurrentMode(): Flow<PlayerMode?>
    fun getCurrentState(): Flow<PlayedListState?>
    fun getCurrentPlayedList(): Flow<PlayerPlayedList?>
    fun getCurrentPosition(): Flow<Int?>
    fun getCurrentProgress(): Flow<Int>
    suspend fun upsertAllMusics(playerMusics: List<PlayerMusic>)
    suspend fun upsertPlayedList(
        playedList: PlayerPlayedList,
        playerMusics: List<PlayerMusic>,
    )
    suspend fun deleteAll(musicIds: List<UUID>)
    suspend fun deletePlayedList(playedListId: UUID)

    suspend fun deleteCurrentPlayedList()

    /**
     * Handle changes in list (adding, removal)
     * This will automatically move the list to the [PlayerMode.Normal] state
     * and adapt the saved list order and content.
     *
     * @param musicIdsToKeep: the list of music ids to keep when switching form loop to shuffle,
     * as we will delete all songs except these in this case.
     */
    suspend fun handleListChange(musicIdsToKeep: List<UUID>)

    suspend fun setState(state: PlayedListState)

    suspend fun setCurrent(musicId: UUID)
    suspend fun setProgress(progress: Int)

    suspend fun switchPlayerMode()

    suspend fun removeCurrentAndPlayNext()
}
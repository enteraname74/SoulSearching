package com.github.enteraname74.soulsearching.repository.datasource.player

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.FullPlayerMusicUser
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayedListToContinue
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerMusicUser
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import com.github.enteraname74.domain.model.player.SharedPlayedListPreview
import com.github.enteraname74.domain.model.player.SharedPlayedListUser
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * Data source of a PlayerMusic.
 */
interface PlayerLocalDataSource {
    fun getAllPaginated(): Flow<PagingData<Music>>
    fun getAll(): Flow<List<Music>>
    fun getSize(): Flow<Int>
    fun getCurrentMusic(): Flow<PlayerMusic?>
    fun getNextMusic(
        musicIdsToSkip: List<Uuid> = emptyList()
    ): Flow<PlayerMusic?>

    fun getLastMusic(): Flow<PlayerMusic?>
    fun getPreviousMusic(): Flow<PlayerMusic?>

    fun getCurrentMode(): Flow<PlayerMode?>
    fun getCurrentState(): Flow<PlayedListState?>
    fun getCurrentPlayedList(): Flow<PlayerPlayedList?>
    fun getCachedPlayedList(playlistId: String): Flow<PlayedListToContinue?>
    fun getCurrentPosition(): Flow<Int?>
    fun getCurrentProgress(): Flow<Int>
    fun getCurrentScope(): Flow<PlayedListScope?>
    suspend fun upsertAllMusics(playerMusics: List<PlayerMusic>)
    suspend fun moveMusic(
        fromMusicId: Uuid,
        toMusicId: Uuid,
    )

    suspend fun upsertPlayedList(
        playedList: PlayerPlayedList,
        playerMusics: List<PlayerMusic>,
    )

    suspend fun deleteAll(musicIds: List<Uuid>)
    suspend fun deletePlayedList(playedListId: Uuid)

    suspend fun deleteCurrentPlayedList()

    /**
     * Handle changes in list (adding, removal)
     * This will automatically move the list to the [PlayerMode.Normal] state
     * and adapt the saved list order and content.
     *
     * @param musicIdsToKeep: the list of music ids to keep when switching form loop to shuffle,
     * as we will delete all songs except these in this case.
     */
    suspend fun handleListChange(musicIdsToKeep: List<Uuid>)

    suspend fun setState(state: PlayedListState)

    suspend fun setScope(scope: PlayedListScope)

    suspend fun continuePlayedList(
        playedListId: Uuid,
    )

    suspend fun setCurrent(musicId: Uuid)
    suspend fun setProgress(progress: Int)

    suspend fun switchPlayerMode()

    suspend fun removeCurrentAndPlayNext()
    suspend fun updatesMusics(
        musicIdsToRemove: List<Uuid>,
        playerMusicsToAdd: List<PlayerMusic>,
    )

    suspend fun setSharedUsers(
        users: List<SharedPlayedListUser>,
    )

    fun observeCurrentSharedUsers(): Flow<List<SharedPlayedListUser>>

    suspend fun setPlayerMusicUsers(playerMusicUsers: List<PlayerMusicUser>)
    fun observeFullPlayerMusicUsers(): Flow<List<FullPlayerMusicUser>>

    suspend fun setSharedPlayedListPreviews(previews: List<SharedPlayedListPreview>)

    fun observeAllSharedPlayedListPreviews(): Flow<List<SharedPlayedListPreview>>

    suspend fun deleteAllSharedPlayedListPreviews()
}

package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Interface representing the PlayerMusicListViewModel and its functions.
 */
interface PlayerMusicListViewModel {
    var state: StateFlow<MusicState>

    /**
     * Retrieve the player music list from the database.
     */
    suspend fun getPlayerMusicList(): ArrayList<Music>

    /**
     * Save the player music list to the database.
     */
    fun savePlayerMusicList(musicList: ArrayList<UUID>)

    /**
     * Reset the player music list to the database.
     */
    fun resetPlayerMusicList()

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent)
}
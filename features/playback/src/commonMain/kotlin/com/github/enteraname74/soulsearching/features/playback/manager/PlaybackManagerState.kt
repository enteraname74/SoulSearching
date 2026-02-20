package com.github.enteraname74.soulsearching.features.playback.manager

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayerMode
import kotlinx.coroutines.flow.Flow

sealed interface PlaybackManagerState {
    data object Stopped: PlaybackManagerState
    data class Data(
        val currentMusic: Music,
        val next: Music?,
        val previous: Music?,
        val isCurrentMusicInFavorite: Boolean,
        val currentMusicIndex: Int,
        val listSize: Int,
        val playerMode: PlayerMode,
        val isPlaying: Boolean,
        val minimisePlayer: Boolean,
    ): PlaybackManagerState

    fun isEmpty(): Boolean =
        (this as? Data)?.listSize?.let { it == 0 } ?: true
}
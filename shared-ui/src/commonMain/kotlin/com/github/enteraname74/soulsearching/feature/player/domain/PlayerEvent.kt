package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlayerMode
import java.util.UUID

/**
 * Events for the player view.
 */
sealed interface PlayerEvent {
    data object ToggleFavoriteState: PlayerEvent
    data class SetPlayedList(val playedList: List<Music>): PlayerEvent
    data class SetPlayerMode(val playerMode: PlayerMode): PlayerEvent
    data class SetCurrentMusic(val currentMusic: Music?): PlayerEvent
    data class SetCurrentMusicPosition(val position: Int): PlayerEvent
    data class SetIsPlaying(val isPlaying: Boolean): PlayerEvent

    data class SetCurrentMusicCover(val cover: ImageBitmap?): PlayerEvent

    data object GetLyrics: PlayerEvent
}
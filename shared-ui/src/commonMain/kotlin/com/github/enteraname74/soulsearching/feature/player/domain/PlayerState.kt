package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import com.github.enteraname74.domain.model.PlayerMode

/**
 * UI state of the player.
 */
data class PlayerState(
    val currentMusic: Music? = null,
    val isCurrentMusicInFavorite: Boolean = false,
    val currentMusicPosition: Int = 0,
    val currentMusicCover: ImageBitmap? = null,
    val allCovers: List<ImageCover> = emptyList(),
    val playedList: List<Music> = emptyList(),
    val isPlaying: Boolean = false,
    val playerMode: PlayerMode = PlayerMode.Normal,
    val playlistsWithMusics: List<PlaylistWithMusics> = emptyList(),
    val currentMusicLyrics: LyricsFetchState = LyricsFetchState.NoLyricsFound,
    val canSwipeCover: Boolean = true,
)

package com.github.soulsearching.player.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.soulsearching.player.domain.model.LyricsFetchState
import com.github.enteraname74.domain.model.PlayerMode

/**
 * UI state of the player.
 */
data class PlayerState(
    val currentMusic: Music? = null,
    val isCurrentMusicInFavorite: Boolean = false,
    val currentMusicPosition: Int = 0,
    val currentMusicCover: ImageBitmap? = null,
    val playedList: List<Music> = emptyList(),
    val isPlaying: Boolean = false,
    val playerMode: PlayerMode = PlayerMode.NORMAL,
    val isDeleteMusicDialogShown: Boolean = false,
    val isAddToPlaylistBottomSheetShown: Boolean = false,
    val isMusicBottomSheetShown: Boolean = false,
    val playlistsWithMusics: List<PlaylistWithMusics> = emptyList(),
    val currentMusicLyrics: LyricsFetchState = LyricsFetchState.NoLyricsFound
)

package com.github.soulsearching.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.util.LyricsProvider
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.player.domain.PlayerViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager

/**
 * Implementation of the PlayerViewModel.
 */
@SuppressLint("MutableCollectionMutableState")
class PlayerViewModelAndroidImpl(
    musicRepository: MusicRepository,
    playbackManager: PlaybackManager,
    musicPlaylistRepository: MusicPlaylistRepository,
    playlistRepository: PlaylistRepository,
    colorThemeManager: ColorThemeManager,
    lyricsProvider: LyricsProvider
): PlayerViewModel, ViewModel() {
    override val handler: PlayerViewModelHandler = PlayerViewModelHandler(
        musicRepository = musicRepository,
        playbackManager = playbackManager,
        colorThemeManager = colorThemeManager,
        musicPlaylistRepository = musicPlaylistRepository,
        playlistRepository = playlistRepository,
        coroutineScope = viewModelScope,
        lyricsProvider = lyricsProvider
    )
}
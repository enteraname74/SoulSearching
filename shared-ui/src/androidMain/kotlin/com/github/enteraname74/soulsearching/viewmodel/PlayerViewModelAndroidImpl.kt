package com.github.enteraname74.soulsearching.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModelHandler
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager

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
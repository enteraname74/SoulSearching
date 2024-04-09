package com.github.soulsearching.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.repository.MusicRepository
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
    colorThemeManager: ColorThemeManager
): PlayerViewModel, ViewModel() {
    override val handler: PlayerViewModelHandler = PlayerViewModelHandler(
        musicRepository = musicRepository,
        playbackManager = playbackManager,
        colorThemeManager = colorThemeManager
    )
}
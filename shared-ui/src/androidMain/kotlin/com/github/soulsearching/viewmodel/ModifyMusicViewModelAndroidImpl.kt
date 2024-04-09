package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager

/**
 * Implementation of the ModifyMusicViewModel.
 */
class ModifyMusicViewModelAndroidImpl(
    musicRepository: MusicRepository,
    imageCoverRepository: ImageCoverRepository,
    playbackManager: PlaybackManager
) : ModifyMusicViewModel, ViewModel() {
    override val handler: ModifyMusicViewModelHandler = ModifyMusicViewModelHandler(
        coroutineScope = viewModelScope,
        musicRepository = musicRepository,
        imageCoverRepository = imageCoverRepository,
        playbackManager = playbackManager
    )
}
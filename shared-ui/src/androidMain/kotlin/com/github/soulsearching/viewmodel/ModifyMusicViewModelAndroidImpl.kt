package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.service.ImageCoverService
import com.github.enteraname74.domain.service.MusicService
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicViewModelHandler

/**
 * Implementation of the ModifyMusicViewModel.
 */
class ModifyMusicViewModelAndroidImpl(
    musicRepository: MusicRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManager,
    musicService: MusicService,
    imageCoverService: ImageCoverService
) : ModifyMusicViewModel, ViewModel() {
    override val handler: ModifyMusicViewModelHandler = ModifyMusicViewModelHandler(
        coroutineScope = viewModelScope,
        musicRepository = musicRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings,
        playbackManager = playbackManager,
        musicService = musicService,
        imageCoverService = imageCoverService
    )
}
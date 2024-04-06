package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModelHandler

/**
 * Implementation of the ModifyPlaylistViewModel.
 */
class ModifyPlaylistViewModelAndroidImpl(
    playlistRepository : PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings
) : ModifyPlaylistViewModel, ViewModel() {
    override val handler: ModifyPlaylistViewModelHandler = ModifyPlaylistViewModelHandler(
        coroutineScope = viewModelScope,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings
    )
}
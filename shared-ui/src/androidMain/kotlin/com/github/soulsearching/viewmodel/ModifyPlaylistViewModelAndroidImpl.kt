package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.ModifyPlaylistViewModelHandler

/**
 * Implementation of the ModifyPlaylistViewModel.
 */
class ModifyPlaylistViewModelAndroidImpl(
    playlistRepository : PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings
) : ModifyPlaylistViewModel {
    override val handler: ModifyPlaylistViewModelHandler = ModifyPlaylistViewModelHandler(
        coroutineScope = screenModelScope,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings
    )
}
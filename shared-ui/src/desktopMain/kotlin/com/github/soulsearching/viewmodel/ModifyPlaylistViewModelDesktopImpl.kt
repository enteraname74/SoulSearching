package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.ModifyPlaylistViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of the ModifyPlaylistViewModel.
 */
class ModifyPlaylistViewModelDesktopImpl(
    playlistRepository : PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings
) : ModifyPlaylistViewModel {
    override val handler: ModifyPlaylistViewModelHandler = ModifyPlaylistViewModelHandler(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings
    )
}
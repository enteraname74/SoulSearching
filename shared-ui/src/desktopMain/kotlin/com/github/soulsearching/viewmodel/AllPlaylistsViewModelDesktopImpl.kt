package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.AllPlaylistsViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of the allPlaylistsViewModel.
 */
class AllPlaylistsViewModelDesktopImpl(
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings
) : AllPlaylistsViewModel {
    override val handler: AllPlaylistsViewModelHandler = AllPlaylistsViewModelHandler(
        coroutineScope = screenModelScope,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings
    )
}
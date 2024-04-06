package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.AllQuickAccessViewModel
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllQuickAccessViewModelHandler

/**
 * Implementation of the allQuickAccessViewModel.
 */
class AllQuickAccessViewModelDesktopImpl(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
) : AllQuickAccessViewModel {
    override val handler: AllQuickAccessViewModelHandler = AllQuickAccessViewModelHandler(
        coroutineScope = screenModelScope,
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository
    )
}
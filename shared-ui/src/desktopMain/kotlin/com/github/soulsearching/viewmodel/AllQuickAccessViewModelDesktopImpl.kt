package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.viewmodel.handler.AllQuickAccessViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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
        coroutineScope = CoroutineScope(Dispatchers.IO),
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository
    )
}
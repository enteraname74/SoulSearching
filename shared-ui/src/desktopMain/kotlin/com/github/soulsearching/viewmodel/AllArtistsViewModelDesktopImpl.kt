package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.AllArtistsViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of the AllArtistsViewModel.
 */
class AllArtistsViewModelDesktopImpl(
    artistRepository: ArtistRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    settings: SoulSearchingSettings
) : AllArtistsViewModel {
    override val handler: AllArtistsViewModelHandler = AllArtistsViewModelHandler(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        artistRepository = artistRepository,
        musicRepository = musicRepository,
        albumRepository = albumRepository,
        settings = settings
    )
}
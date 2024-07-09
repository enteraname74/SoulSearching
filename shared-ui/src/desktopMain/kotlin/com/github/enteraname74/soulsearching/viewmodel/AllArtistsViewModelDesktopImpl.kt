package com.github.enteraname74.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.AllArtistsViewModel
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.AllArtistsViewModelHandler

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
        coroutineScope = screenModelScope,
        artistRepository = artistRepository,
        musicRepository = musicRepository,
        albumRepository = albumRepository,
        settings = settings
    )
}
package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.AllArtistsViewModelHandler

/**
 * Implementation of the AllArtistsViewModel.
 */
class AllArtistsViewModelAndroidImpl(
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
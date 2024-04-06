package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllAlbumsViewModeHandler

/**
 * Implementation of the AllAlbumsViewModel.
 */
class AllAlbumsViewModelDesktopImpl(
    albumRepository: AlbumRepository,
    musicRepository: MusicRepository,
    artistRepository: ArtistRepository,
    musicArtistRepository: MusicArtistRepository,
    settings: SoulSearchingSettings
) : AllAlbumsViewModel {
    override val handler: AllAlbumsViewModeHandler = AllAlbumsViewModeHandler(
        coroutineScope = screenModelScope,
        albumRepository = albumRepository,
        musicRepository = musicRepository,
        artistRepository = artistRepository,
        musicArtistRepository = musicArtistRepository,
        settings = settings
    )
}
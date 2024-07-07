package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.AllArtistsViewModel
import com.github.enteraname74.domain.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.AllArtistsViewModelHandler

/**
 * Implementation of the AllArtistsViewModel.
 */
class AllArtistsViewModelAndroidImpl(
    artistRepository: ArtistRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    settings: SoulSearchingSettings
) : AllArtistsViewModel, ViewModel() {
    override val handler: AllArtistsViewModelHandler = AllArtistsViewModelHandler(
        coroutineScope = viewModelScope,
        artistRepository = artistRepository,
        musicRepository = musicRepository,
        albumRepository = albumRepository,
        settings = settings
    )
}
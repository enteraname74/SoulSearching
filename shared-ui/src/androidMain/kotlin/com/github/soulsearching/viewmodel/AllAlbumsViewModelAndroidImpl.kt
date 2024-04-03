package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.AllAlbumsViewModeHandler

/**
 * Implementation of the AllAlbumsViewModel.
 */
class AllAlbumsViewModelAndroidImpl(
    albumRepository: AlbumRepository,
    musicRepository: MusicRepository,
    artistRepository: ArtistRepository,
    musicArtistRepository: MusicArtistRepository,
    settings: SoulSearchingSettings
) : AllAlbumsViewModel, ViewModel() {
    override val handler: AllAlbumsViewModeHandler = AllAlbumsViewModeHandler(
        coroutineScope = viewModelScope,
        albumRepository = albumRepository,
        musicRepository = musicRepository,
        artistRepository = artistRepository,
        musicArtistRepository = musicArtistRepository,
        settings = settings
    )
}
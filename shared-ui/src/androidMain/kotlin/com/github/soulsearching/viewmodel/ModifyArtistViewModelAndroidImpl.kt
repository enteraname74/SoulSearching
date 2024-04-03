package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.viewmodel.handler.ModifyArtistViewModelHandler

/**
 * Implementation of the ModifyArtistViewModel.
 */
class ModifyArtistViewModelAndroidImpl(
    musicRepository: MusicRepository,
    artistRepository: ArtistRepository,
    musicArtistRepository: MusicArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    albumRepository: AlbumRepository,
    imageCoverRepository: ImageCoverRepository
) : ModifyArtistViewModel, ViewModel() {
    override val handler: ModifyArtistViewModelHandler = ModifyArtistViewModelHandler(
        coroutineScope = viewModelScope,
        musicRepository = musicRepository,
        artistRepository = artistRepository,
        musicArtistRepository = musicArtistRepository,
        musicAlbumRepository = musicAlbumRepository,
        albumArtistRepository = albumArtistRepository,
        albumRepository = albumRepository,
        imageCoverRepository = imageCoverRepository
    )
}
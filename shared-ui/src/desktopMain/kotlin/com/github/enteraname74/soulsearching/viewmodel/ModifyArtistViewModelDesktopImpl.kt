package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.ModifyArtistViewModel
import com.github.soulsearching.modifyelement.modifyartist.domain.ModifyArtistViewModelHandler

/**
 * Implementation of the ModifyArtistViewModel.
 */
class ModifyArtistViewModelDesktopImpl(
    artistRepository: ArtistRepository,
    imageCoverRepository: ImageCoverRepository
) : ModifyArtistViewModel {
    override val handler: ModifyArtistViewModelHandler = ModifyArtistViewModelHandler(
        coroutineScope = screenModelScope,
        artistRepository = artistRepository,
        imageCoverRepository = imageCoverRepository
    )
}
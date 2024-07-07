package com.github.enteraname74.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistViewModelHandler

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
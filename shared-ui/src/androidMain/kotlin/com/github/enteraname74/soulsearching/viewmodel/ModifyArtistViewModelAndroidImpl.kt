package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistViewModelHandler

/**
 * Implementation of the ModifyArtistViewModel.
 */
class ModifyArtistViewModelAndroidImpl(
    artistRepository: ArtistRepository,
    imageCoverRepository: ImageCoverRepository
) : ModifyArtistViewModel, ViewModel() {
    override val handler: ModifyArtistViewModelHandler = ModifyArtistViewModelHandler(
        coroutineScope = viewModelScope,
        artistRepository = artistRepository,
        imageCoverRepository = imageCoverRepository
    )
}
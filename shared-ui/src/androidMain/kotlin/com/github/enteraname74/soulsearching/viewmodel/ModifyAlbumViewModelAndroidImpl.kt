package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain.ModifyAlbumViewModelHandler

/**
 * Implementation of the ModifyAlbumViewModel.
 */
class ModifyAlbumViewModelAndroidImpl(
    albumRepository: AlbumRepository,
    imageCoverRepository: ImageCoverRepository,
    artistRepository: ArtistRepository,
    playbackManager: PlaybackManager
) : ModifyAlbumViewModel, ViewModel() {
    override val handler: ModifyAlbumViewModelHandler = ModifyAlbumViewModelHandler(
        coroutineScope = viewModelScope,
        albumRepository = albumRepository,
        imageCoverRepository = imageCoverRepository,
        playbackManager = playbackManager,
        artistRepository = artistRepository
    )
}
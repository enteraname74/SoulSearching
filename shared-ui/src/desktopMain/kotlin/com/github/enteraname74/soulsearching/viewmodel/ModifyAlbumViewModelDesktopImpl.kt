package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.modifyelement.modifyalbum.domain.ModifyAlbumViewModelHandler

/**
 * Implementation of the ModifyAlbumViewModel.
 */
class ModifyAlbumViewModelDesktopImpl(
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    playbackManager: PlaybackManagerDesktopImpl
) : ModifyAlbumViewModel {
    override val handler: ModifyAlbumViewModelHandler = ModifyAlbumViewModelHandler(
        coroutineScope = screenModelScope,
        albumRepository = albumRepository,
        imageCoverRepository = imageCoverRepository,
        playbackManager = playbackManager,
        artistRepository = artistRepository
    )
}
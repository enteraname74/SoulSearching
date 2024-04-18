package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.modifyelement.modifyalbum.domain.ModifyAlbumViewModelHandler

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
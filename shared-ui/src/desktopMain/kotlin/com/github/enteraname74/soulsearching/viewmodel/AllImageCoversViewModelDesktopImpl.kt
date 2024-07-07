package com.github.enteraname74.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.feature.coversprovider.domain.AllImageCoversViewModelHandler
import com.github.enteraname74.soulsearching.domain.viewmodel.AllImageCoversViewModel

/**
 * Implementation of the AllImageCoversViewModel.
 */
class AllImageCoversViewModelDesktopImpl(
    imageCoverRepository: ImageCoverRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    playlistRepository: PlaylistRepository
) : AllImageCoversViewModel {
    override val handler: AllImageCoversViewModelHandler = AllImageCoversViewModelHandler(
        coroutineScope = screenModelScope,
        imageCoverRepository = imageCoverRepository,
        musicRepository = musicRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        playlistRepository = playlistRepository
    )
}
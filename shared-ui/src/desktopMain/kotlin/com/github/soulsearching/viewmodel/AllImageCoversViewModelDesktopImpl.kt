package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.viewmodel.handler.AllImageCoversViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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
        coroutineScope = CoroutineScope(Dispatchers.IO),
        imageCoverRepository = imageCoverRepository,
        musicRepository = musicRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        playlistRepository = playlistRepository
    )
}
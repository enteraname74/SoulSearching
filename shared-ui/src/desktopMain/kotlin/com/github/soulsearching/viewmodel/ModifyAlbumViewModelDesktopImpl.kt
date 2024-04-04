package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.viewmodel.handler.ModifyAlbumViewModelHandler

/**
 * Implementation of the ModifyAlbumViewModel.
 */
class ModifyAlbumViewModelDesktopImpl(
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicArtistRepository: MusicArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    playbackManager: PlaybackManagerDesktopImpl
) : ModifyAlbumViewModel {
    override val handler: ModifyAlbumViewModelHandler = ModifyAlbumViewModelHandler(
        coroutineScope = screenModelScope,
        musicRepository = musicRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        musicArtistRepository = musicArtistRepository,
        musicAlbumRepository = musicAlbumRepository,
        albumArtistRepository = albumArtistRepository,
        imageCoverRepository = imageCoverRepository,
        playbackManager = playbackManager
    )
}
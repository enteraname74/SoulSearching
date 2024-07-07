package com.github.enteraname74.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain.SelectedAlbumViewModelHandler
import com.github.enteraname74.soulsearching.model.PlaybackManagerDesktopImpl

/**
 * Implementation of the SelectedAlbumViewModel.
 */
class SelectedAlbumViewModelDesktopImpl(
    albumRepository: AlbumRepository,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    playbackManager: PlaybackManagerDesktopImpl
) : SelectedAlbumViewModel {
    override val handler: SelectedAlbumViewModelHandler = SelectedAlbumViewModelHandler(
        coroutineScope = screenModelScope,
        albumRepository = albumRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicRepository = musicRepository,
        playbackManager = playbackManager,
        playlistRepository = playlistRepository
    )
}
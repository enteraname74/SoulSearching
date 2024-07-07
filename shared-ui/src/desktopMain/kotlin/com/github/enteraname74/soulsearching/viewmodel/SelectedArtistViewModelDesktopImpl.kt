package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.elementpage.artistpage.domain.SelectedArtistViewModelHandler
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of the SelectedArtistViewModel.
 */
class SelectedArtistViewModelDesktopImpl(
    artistRepository: ArtistRepository,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    playbackManager: PlaybackManagerDesktopImpl,
    albumRepository: AlbumRepository
) : SelectedArtistViewModel {
    override val handler: SelectedArtistViewModelHandler = SelectedArtistViewModelHandler(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        artistRepository = artistRepository,
        playbackManager = playbackManager,
        musicRepository = musicRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository
    )
}
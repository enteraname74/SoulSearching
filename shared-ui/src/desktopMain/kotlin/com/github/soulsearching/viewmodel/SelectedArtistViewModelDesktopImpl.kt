package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.SelectedArtistViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of the SelectedArtistViewModel.
 */
class SelectedArtistViewModelDesktopImpl(
    artistRepository: ArtistRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumArtistRepository: AlbumArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManagerDesktopImpl
) : SelectedArtistViewModel {
    override val handler: SelectedArtistViewModelHandler = SelectedArtistViewModelHandler(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        artistRepository = artistRepository,
        musicRepository = musicRepository,
        albumRepository = albumRepository,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        albumArtistRepository = albumArtistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings,
        playbackManager = playbackManager
    )
}
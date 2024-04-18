package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
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
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.elementpage.albumpage.domain.SelectedAlbumViewModelHandler
import com.github.soulsearching.domain.viewmodel.SelectedAlbumViewModel

/**
 * Implementation of the SelectedAlbumViewModel.
 */
class SelectedAlbumViewModelDesktopImpl(
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManagerDesktopImpl
) : SelectedAlbumViewModel {
    override val handler: SelectedAlbumViewModelHandler = SelectedAlbumViewModelHandler(
        coroutineScope = screenModelScope,
        albumRepository = albumRepository,
        settings = settings
    )
}
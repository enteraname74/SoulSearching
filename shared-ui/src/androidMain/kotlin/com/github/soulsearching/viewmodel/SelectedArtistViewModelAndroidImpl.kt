package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.elementpage.artistpage.domain.SelectedArtistViewModelHandler
import com.github.soulsearching.domain.viewmodel.SelectedArtistViewModel

/**
 * Implementation of the SelectedArtistViewModel.
 */
class SelectedArtistViewModelAndroidImpl(
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
    playbackManager: PlaybackManager
) : SelectedArtistViewModel, ViewModel() {
    override val handler: SelectedArtistViewModelHandler = SelectedArtistViewModelHandler(
        coroutineScope = viewModelScope,
        artistRepository = artistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicRepository = musicRepository,
        playbackManager = playbackManager,
        playlistRepository = playlistRepository
    )
}
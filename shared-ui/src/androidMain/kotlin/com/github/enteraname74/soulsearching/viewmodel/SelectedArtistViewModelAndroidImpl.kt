package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain.SelectedArtistViewModelHandler
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedArtistViewModel

/**
 * Implementation of the SelectedArtistViewModel.
 */
class SelectedArtistViewModelAndroidImpl(
    artistRepository: ArtistRepository,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    playbackManager: PlaybackManager
) : SelectedArtistViewModel, ViewModel() {
    override val handler: SelectedArtistViewModelHandler = SelectedArtistViewModelHandler(
        coroutineScope = viewModelScope,
        artistRepository = artistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicRepository = musicRepository,
        playbackManager = playbackManager,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository
    )
}
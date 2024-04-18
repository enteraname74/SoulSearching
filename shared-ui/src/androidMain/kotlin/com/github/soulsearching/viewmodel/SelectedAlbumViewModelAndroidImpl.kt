package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.SelectedAlbumViewModel
import com.github.soulsearching.elementpage.albumpage.domain.SelectedAlbumViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager

/**
 * Implementation of the SelectedAlbumViewModel.
 */
class SelectedAlbumViewModelAndroidImpl(
    albumRepository: AlbumRepository,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    playbackManager: PlaybackManager
) : SelectedAlbumViewModel, ViewModel() {
    override val handler: SelectedAlbumViewModelHandler = SelectedAlbumViewModelHandler(
        coroutineScope = viewModelScope,
        albumRepository = albumRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicRepository = musicRepository,
        playbackManager = playbackManager,
        playlistRepository = playlistRepository
    )
}
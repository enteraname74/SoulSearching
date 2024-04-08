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
import com.github.soulsearching.domain.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.elementpage.playlistpage.domain.SelectedPlaylistViewModelHandler

/**
 * Implementation of the SelectedPlaylistViewModel.
 */
class SelectedPlaylistViewModelAndroidImpl(
    playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    artistRepository: ArtistRepository,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManager
) : SelectedPlaylistViewModel, ViewModel() {
    override val handler: SelectedPlaylistViewModelHandler = SelectedPlaylistViewModelHandler(
        coroutineScope = viewModelScope,
        playlistRepository = playlistRepository
    )
}
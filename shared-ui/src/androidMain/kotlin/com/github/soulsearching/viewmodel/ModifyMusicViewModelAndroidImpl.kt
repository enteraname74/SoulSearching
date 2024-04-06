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
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicViewModelHandler

/**
 * Implementation of the ModifyMusicViewModel.
 */
class ModifyMusicViewModelAndroidImpl(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    artistRepository: ArtistRepository,
    albumRepository: AlbumRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    musicArtistRepository: MusicArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManager
) : ModifyMusicViewModel, ViewModel() {
    override val handler: ModifyMusicViewModelHandler = ModifyMusicViewModelHandler(
        coroutineScope = viewModelScope,
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        artistRepository = artistRepository,
        albumRepository = albumRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicAlbumRepository = musicAlbumRepository,
        albumArtistRepository = albumArtistRepository,
        musicArtistRepository = musicArtistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings,
        playbackManager = playbackManager
    )
}
package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.viewmodel.handler.ModifyMusicViewModelHandler

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
    playbackManager: PlaybackManagerAndroidImpl
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
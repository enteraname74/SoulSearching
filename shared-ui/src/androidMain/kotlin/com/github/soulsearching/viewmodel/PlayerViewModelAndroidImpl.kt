package com.github.soulsearching.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
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
import com.github.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.viewmodel.handler.PlayerViewModelHandler

/**
 * Implementation of the PlayerViewModel.
 */
@SuppressLint("MutableCollectionMutableState")
class PlayerViewModelAndroidImpl(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManagerAndroidImpl,
    colorThemeManager: ColorThemeManager
): PlayerViewModel, ViewModel() {
    override val handler: PlayerViewModelHandler = PlayerViewModelHandler(
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        albumArtistRepository = albumArtistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings,
        playbackManager = playbackManager,
        colorThemeManager = colorThemeManager
    )
}
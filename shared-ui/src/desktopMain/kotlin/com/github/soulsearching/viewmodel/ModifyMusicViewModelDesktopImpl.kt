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
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicViewModelHandler

/**
 * Implementation of the ModifyMusicViewModel.
 */
class ModifyMusicViewModelDesktopImpl(
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
    playbackManager: PlaybackManagerDesktopImpl
) : ModifyMusicViewModel {
    override val handler: ModifyMusicViewModelHandler = ModifyMusicViewModelHandler(
        coroutineScope = screenModelScope,
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
package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.classes.MusicFetcherDesktopImpl
import com.github.soulsearching.classes.PlaybackManagerDesktopImpl
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.AllMusicsViewModelDesktopHandler
import com.github.soulsearching.viewmodel.handler.AllMusicsViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of the AllMusicsViewModel.
 */
class AllMusicsViewModelDesktopImpl(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    folderRepository: FolderRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManagerDesktopImpl,
    musicFetcher: MusicFetcherDesktopImpl
) : AllMusicsViewModel {
    override val handler: AllMusicsViewModelHandler = AllMusicsViewModelDesktopHandler(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        albumArtistRepository = albumArtistRepository,
        imageCoverRepository = imageCoverRepository,
        folderRepository = folderRepository,
        settings = settings,
        playbackManager = playbackManager,
        musicFetcher = musicFetcher
    )
}
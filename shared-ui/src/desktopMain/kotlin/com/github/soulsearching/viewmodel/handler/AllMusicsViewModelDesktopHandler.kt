package com.github.soulsearching.viewmodel.handler

import com.github.enteraname74.domain.repository.*
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllMusicsViewModelHandler
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.domain.model.MusicFetcher
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import kotlinx.coroutines.CoroutineScope

class AllMusicsViewModelDesktopHandler(
    coroutineScope: CoroutineScope,
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
    musicFetcher: MusicFetcher,
    playbackManager: PlaybackManagerDesktopImpl
) : AllMusicsViewModelHandler(
    coroutineScope = coroutineScope,
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
    musicFetcher = musicFetcher,
    playbackManager = playbackManager
) {
    override fun checkAndDeleteMusicIfNotExist() {}
}
package com.github.soulsearching.viewmodel.handler

import com.github.enteraname74.domain.repository.*
import com.github.soulsearching.classes.PlaybackManagerDesktopImpl
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.model.settings.SoulSearchingSettings
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
    folderRepository: FolderRepository,
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
    folderRepository = folderRepository,
    settings = settings,
    musicFetcher = musicFetcher,
    playbackManager = playbackManager
) {
    override fun checkAndDeleteMusicIfNotExist() {}
}
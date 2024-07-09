package com.github.enteraname74.soulsearching.viewmodel.handler

import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.AllMusicsViewModelHandler
import com.github.enteraname74.soulsearching.model.PlaybackManagerDesktopImpl
import kotlinx.coroutines.CoroutineScope

class AllMusicsViewModelDesktopHandler(
    coroutineScope: CoroutineScope,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    settings: SoulSearchingSettings,
    musicFetcher: MusicFetcher,
    playbackManager: PlaybackManagerDesktopImpl
) : AllMusicsViewModelHandler(
    coroutineScope = coroutineScope,
    musicRepository = musicRepository,
    musicAlbumRepository = musicAlbumRepository,
    musicArtistRepository = musicArtistRepository,
    settings = settings,
    musicFetcher = musicFetcher,
    playlistRepository = playlistRepository,
    playbackManager = playbackManager
) {
    override fun checkAndDeleteMusicIfNotExist() {}
}
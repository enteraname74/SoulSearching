package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.domain.viewmodel.AllMusicsViewModel
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllMusicsViewModelHandler
import com.github.soulsearching.model.MusicFetcherDesktopImpl
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.viewmodel.handler.AllMusicsViewModelDesktopHandler

/**
 * Implementation of the AllMusicsViewModel.
 */
class AllMusicsViewModelDesktopImpl(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManagerDesktopImpl,
    musicFetcher: MusicFetcherDesktopImpl
) : AllMusicsViewModel {
    override val handler: AllMusicsViewModelHandler = AllMusicsViewModelDesktopHandler(
        coroutineScope = screenModelScope,
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        settings = settings,
        musicFetcher = musicFetcher,
        playbackManager = playbackManager
    )
}
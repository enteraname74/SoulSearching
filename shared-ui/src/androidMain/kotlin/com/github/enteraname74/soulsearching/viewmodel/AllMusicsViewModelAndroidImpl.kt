package com.github.enteraname74.soulsearching.viewmodel

import android.content.Context
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.model.utils.MusicFetcherAndroidImpl
import com.github.enteraname74.domain.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.viewmodel.handler.AllMusicsViewModelAndroidHandler
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.AllMusicsViewModelHandler

/**
 * Implementation of the AllMusicsViewModel.
 */
class AllMusicsViewModelAndroidImpl(
    context: Context,
    musicRepository: MusicRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManager,
    musicFetcher: MusicFetcherAndroidImpl,
    playlistRepository: PlaylistRepository
) : AllMusicsViewModel {
    override val handler: AllMusicsViewModelHandler = AllMusicsViewModelAndroidHandler(
        context = context,
        coroutineScope = screenModelScope,
        musicRepository = musicRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        settings = settings,
        musicFetcher = musicFetcher,
        playbackManager = playbackManager,
        playlistRepository = playlistRepository
    )
}
package com.github.soulsearching.viewmodel

import android.content.Context
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.AllMusicsViewModel
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.model.utils.MusicFetcherAndroidImpl
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.AllMusicsViewModelAndroidHandler
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllMusicsViewModelHandler

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
    )
}
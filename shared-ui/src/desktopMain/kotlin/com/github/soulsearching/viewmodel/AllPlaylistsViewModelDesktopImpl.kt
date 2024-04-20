package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllPlaylistsViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager

/**
 * Implementation of the allPlaylistsViewModel.
 */
class AllPlaylistsViewModelDesktopImpl(
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    musicRepository: MusicRepository,
    playbackManager: PlaybackManager,
    settings: SoulSearchingSettings
) : AllPlaylistsViewModel {
    override val handler: AllPlaylistsViewModelHandler = AllPlaylistsViewModelHandler(
        coroutineScope = screenModelScope,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        settings = settings,
        musicRepository = musicRepository,
        playbackManager = playbackManager
    )
}
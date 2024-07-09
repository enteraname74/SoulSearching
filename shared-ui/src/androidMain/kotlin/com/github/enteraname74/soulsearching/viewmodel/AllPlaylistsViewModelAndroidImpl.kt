package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.AllPlaylistsViewModelHandler
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager

/**
 * Implementation of the allPlaylistsViewModel.
 */
class AllPlaylistsViewModelAndroidImpl(
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    settings: SoulSearchingSettings,
    musicRepository: MusicRepository,
    playbackManager: PlaybackManager
) : AllPlaylistsViewModel, ViewModel() {
    override val handler: AllPlaylistsViewModelHandler = AllPlaylistsViewModelHandler(
        coroutineScope = viewModelScope,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        settings = settings,
        musicRepository = musicRepository,
        playbackManager = playbackManager
    )
}
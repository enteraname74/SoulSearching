package com.github.soulsearching.modifyelement.modifyplaylist.domain

import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.domain.events.handlers.PlaylistEventHandler
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.state.PlaylistState
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * Handler for managing the ModifyPlaylistViewModel.
 */
class ModifyPlaylistViewModelHandler(
    coroutineScope: CoroutineScope,
    playlistRepository : PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings
) : ViewModelHandler {

    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistState()
    )

    private val playlistEventHandler = PlaylistEventHandler(
        privateState = _state,
        publicState = state,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings
    )

    /**
     * Manage playlist events.
     */
    fun onPlaylistEvent(event: PlaylistEvent) {
        playlistEventHandler.handleEvent(event)
    }
}
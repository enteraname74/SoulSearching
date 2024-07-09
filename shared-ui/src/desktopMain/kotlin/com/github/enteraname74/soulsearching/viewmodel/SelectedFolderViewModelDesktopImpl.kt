package com.github.enteraname74.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedFolderViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderViewModelHandler
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager

/**
 * Implementation of the SelectedFolderViewModel for Desktop
 */
class SelectedFolderViewModelDesktopImpl(
    playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    playbackManager: PlaybackManager
) : SelectedFolderViewModel {
    override val handler: SelectedFolderViewModelHandler = SelectedFolderViewModelHandler(
        coroutineScope = screenModelScope,
        playlistRepository = playlistRepository,
        musicRepository = musicRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        playbackManager = playbackManager
    )
}
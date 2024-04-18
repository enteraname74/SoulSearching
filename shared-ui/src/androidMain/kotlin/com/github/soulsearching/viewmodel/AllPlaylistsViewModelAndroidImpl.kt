package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllPlaylistsViewModelHandler

/**
 * Implementation of the allPlaylistsViewModel.
 */
class AllPlaylistsViewModelAndroidImpl(
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings
) : AllPlaylistsViewModel, ViewModel() {
    override val handler: AllPlaylistsViewModelHandler = AllPlaylistsViewModelHandler(
        coroutineScope = viewModelScope,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings
    )
}
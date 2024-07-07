package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.AllQuickAccessViewModel
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllQuickAccessViewModelHandler

/**
 * Implementation of the allQuickAccessViewModel.
 */
class AllQuickAccessViewModelAndroidImpl(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
) : AllQuickAccessViewModel, ViewModel() {
    override val handler: AllQuickAccessViewModelHandler = AllQuickAccessViewModelHandler(
        coroutineScope = viewModelScope,
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository
    )
}
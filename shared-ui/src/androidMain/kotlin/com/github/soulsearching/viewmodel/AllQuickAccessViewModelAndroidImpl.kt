package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.viewmodel.handler.AllQuickAccessViewModelHandler

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
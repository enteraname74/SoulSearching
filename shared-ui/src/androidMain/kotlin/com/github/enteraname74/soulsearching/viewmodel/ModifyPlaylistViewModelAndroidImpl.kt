package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModelHandler

/**
 * Implementation of the ModifyPlaylistViewModel.
 */
class ModifyPlaylistViewModelAndroidImpl(
    playlistRepository : PlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
) : ModifyPlaylistViewModel, ViewModel() {
    override val handler: ModifyPlaylistViewModelHandler = ModifyPlaylistViewModelHandler(
        coroutineScope = viewModelScope,
        playlistRepository = playlistRepository,
        imageCoverRepository = imageCoverRepository,
    )
}
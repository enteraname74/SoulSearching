package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModelHandler

/**
 * Implementation of the ModifyPlaylistViewModel.
 */
class ModifyPlaylistViewModelDesktopImpl(
    playlistRepository : PlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
) : ModifyPlaylistViewModel {
    override val handler: ModifyPlaylistViewModelHandler = ModifyPlaylistViewModelHandler(
        coroutineScope = screenModelScope,
        playlistRepository = playlistRepository,
        imageCoverRepository = imageCoverRepository,
    )
}
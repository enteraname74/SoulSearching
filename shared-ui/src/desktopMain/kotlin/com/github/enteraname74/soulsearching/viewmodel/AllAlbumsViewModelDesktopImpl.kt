package com.github.enteraname74.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.AllAlbumsViewModel
import com.github.enteraname74.domain.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.AllAlbumsViewModeHandler

/**
 * Implementation of the AllAlbumsViewModel.
 */
class AllAlbumsViewModelDesktopImpl(
    albumRepository: AlbumRepository,
    settings: SoulSearchingSettings
) : AllAlbumsViewModel {
    override val handler: AllAlbumsViewModeHandler = AllAlbumsViewModeHandler(
        coroutineScope = screenModelScope,
        albumRepository = albumRepository,
        settings = settings
    )
}
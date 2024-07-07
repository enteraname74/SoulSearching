package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.AllAlbumsViewModel
import com.github.enteraname74.domain.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.AllAlbumsViewModeHandler

/**
 * Implementation of the AllAlbumsViewModel.
 */
class AllAlbumsViewModelAndroidImpl(
    albumRepository: AlbumRepository,
    settings: SoulSearchingSettings
) : AllAlbumsViewModel, ViewModel() {
    override val handler: AllAlbumsViewModeHandler = AllAlbumsViewModeHandler(
        coroutineScope = viewModelScope,
        albumRepository = albumRepository,
        settings = settings
    )
}
package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.classes.utils.MusicFetcherAndroidImpl
import com.github.soulsearching.states.AddMusicsState
import com.github.soulsearching.viewmodel.handler.SettingsAddMusicsViewModelHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of the SettingsAddMusicsViewModel.
 */
class SettingsAddMusicsViewModelAndroidImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    musicFetcher: MusicFetcherAndroidImpl
) : ViewModel(), SettingsAddMusicsViewModel {
    private var _state = MutableStateFlow(AddMusicsState())
    val state = _state.asStateFlow()

    override val handler: SettingsAddMusicsViewModelHandler = SettingsAddMusicsViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
        musicFetcher = musicFetcher
    )
}
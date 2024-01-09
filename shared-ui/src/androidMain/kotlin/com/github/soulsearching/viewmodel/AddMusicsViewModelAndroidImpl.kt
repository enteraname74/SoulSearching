package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.states.AddMusicsState
import com.github.soulsearching.viewmodel.handler.AddMusicsViewModelHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of the AddMusicsViewModel.
 */
class AddMusicsViewModelAndroidImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    musicFetcher: MusicFetcher
) : ViewModel(), AddMusicsViewModel {
    private var _state = MutableStateFlow(AddMusicsState())
    val state = _state.asStateFlow()

    override val handler: AddMusicsViewModelHandler = AddMusicsViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
        musicFetcher = musicFetcher
    )
}
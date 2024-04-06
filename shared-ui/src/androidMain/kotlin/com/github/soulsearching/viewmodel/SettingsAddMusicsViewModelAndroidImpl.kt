package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.SettingsAddMusicsViewModel
import com.github.soulsearching.model.utils.MusicFetcherAndroidImpl
import com.github.soulsearching.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModelHandler

/**
 * Implementation of the SettingsAddMusicsViewModel.
 */
class SettingsAddMusicsViewModelAndroidImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    musicFetcher: MusicFetcherAndroidImpl,
) : SettingsAddMusicsViewModel, ViewModel() {
    override val handler: SettingsAddMusicsViewModelHandler = SettingsAddMusicsViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
        musicFetcher = musicFetcher,
    )
}
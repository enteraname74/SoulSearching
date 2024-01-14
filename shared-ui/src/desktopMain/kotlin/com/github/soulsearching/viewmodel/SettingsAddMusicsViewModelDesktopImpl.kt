package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.classes.MusicFetcherDesktopImpl
import com.github.soulsearching.viewmodel.handler.SettingsAddMusicsViewModelHandler

/**
 * Implementation of the SettingsAddMusicsViewModel.
 */
class SettingsAddMusicsViewModelDesktopImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    musicFetcher: MusicFetcherDesktopImpl
) : SettingsAddMusicsViewModel {
    override val handler: SettingsAddMusicsViewModelHandler = SettingsAddMusicsViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
        musicFetcher = musicFetcher
    )
}
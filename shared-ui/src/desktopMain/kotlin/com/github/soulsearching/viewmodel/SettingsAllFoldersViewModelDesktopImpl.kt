package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.service.MusicService
import com.github.soulsearching.domain.viewmodel.SettingsAllFoldersViewModel
import com.github.soulsearching.settings.managemusics.managefolders.domain.SettingsAllFolderViewModelHandler

/**
 * Implementation of the SettingsAllFoldersViewModel.
 */
class SettingsAllFoldersViewModelDesktopImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    musicService: MusicService
) : SettingsAllFoldersViewModel {
    override val handler: SettingsAllFolderViewModelHandler = SettingsAllFolderViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
        musicService = musicService
    )
}
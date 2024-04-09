package com.github.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.service.MusicRepository
import com.github.soulsearching.domain.viewmodel.SettingsAllFoldersViewModel
import com.github.soulsearching.settings.managemusics.managefolders.domain.SettingsAllFolderViewModelHandler

/**
 * Implementation of the SettingsAllFoldersViewModel.
 */
class SettingsAllFoldersViewModelDesktopImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    musicService: MusicRepository
) : SettingsAllFoldersViewModel {
    override val handler: SettingsAllFolderViewModelHandler = SettingsAllFolderViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
        musicService = musicService
    )
}
package com.github.enteraname74.soulsearching.viewmodel

import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.SettingsAllFoldersViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.SettingsAllFolderViewModelHandler

/**
 * Implementation of the SettingsAllFoldersViewModel.
 */
class SettingsAllFoldersViewModelDesktopImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
) : SettingsAllFoldersViewModel {
    override val handler: SettingsAllFolderViewModelHandler = SettingsAllFolderViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
    )
}
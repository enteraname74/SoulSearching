package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.SettingsAllFoldersViewModel
import com.github.soulsearching.settings.managemusics.managefolders.domain.SettingsAllFolderViewModelHandler

/**
 * Implementation of the SettingsAllFoldersViewModel.
 */
class SettingsAllFoldersViewModelAndroidImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository
) : SettingsAllFoldersViewModel, ViewModel() {
    override val handler: SettingsAllFolderViewModelHandler = SettingsAllFolderViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository
    )
}
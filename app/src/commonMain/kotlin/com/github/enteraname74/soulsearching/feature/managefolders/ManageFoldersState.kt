package com.github.enteraname74.soulsearching.feature.managefolders

import com.github.enteraname74.domain.model.Folder

/**
 * State for managing folders.
 */
data class ManageFoldersState(
    val folders: List<Folder>,
    val navigateBack: (() -> Unit)?,
    val onDone: () -> Unit,
    val setStatus: (
        folder: Folder,
        isSelected: Boolean
    ) -> Unit,
)

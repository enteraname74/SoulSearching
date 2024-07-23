package com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Events of the selected folder screen.
 */
sealed interface SelectedFolderEvent {
    data class SetSelectedFolder(val folderPath: String): SelectedFolderEvent
    data class AddNbPlayed(val playlistId: UUID): SelectedFolderEvent
}
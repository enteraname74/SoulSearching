package com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Events of the selected folder screen.
 */
sealed interface SelectedFolderEvent {
    data class SetSelectedFolder(val folderPath: String): SelectedFolderEvent

    data class SetDeleteMusicDialogVisibility(val isShown: Boolean): SelectedFolderEvent

    data class SetMusicBottomSheetVisibility(val isShown: Boolean): SelectedFolderEvent

    data class SetAddToPlaylistBottomSheetVisibility(val isShown: Boolean): SelectedFolderEvent

    data class DeleteMusic(val musicId: UUID): SelectedFolderEvent

    data class ToggleQuickAccessState(val music: Music): SelectedFolderEvent

    data class AddMusicToPlaylists(val musicId: UUID, val selectedPlaylistsIds: List<UUID>): SelectedFolderEvent

    data class AddNbPlayed(val playlistId: UUID): SelectedFolderEvent
}
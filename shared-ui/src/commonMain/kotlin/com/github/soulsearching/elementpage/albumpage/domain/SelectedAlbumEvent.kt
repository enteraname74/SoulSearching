package com.github.soulsearching.elementpage.albumpage.domain

import com.github.soulsearching.elementpage.artistpage.domain.SelectedArtistEvent
import java.util.UUID

/**
 * Events for the selected album screen.
 */
sealed interface SelectedAlbumEvent {
    data class SetSelectedAlbum(val albumId: UUID): SelectedAlbumEvent

    data class SetDeleteMusicDialogVisibility(val isShown: Boolean): SelectedAlbumEvent

    data class SetMusicBottomSheetVisibility(val isShown: Boolean): SelectedAlbumEvent

    data class SetAddToPlaylistBottomSheetVisibility(val isShown: Boolean): SelectedAlbumEvent

    data class DeleteMusic(val musicId: UUID): SelectedAlbumEvent

    data class ToggleQuickAccessState(val musicId: UUID): SelectedAlbumEvent

    data class AddMusicToPlaylists(val musicId: UUID, val selectedPlaylistsIds: List<UUID>):
        SelectedAlbumEvent

    data class AddNbPlayed(val albumId: UUID): SelectedAlbumEvent
}
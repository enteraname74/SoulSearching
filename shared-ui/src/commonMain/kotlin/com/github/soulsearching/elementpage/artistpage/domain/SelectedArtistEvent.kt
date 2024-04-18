package com.github.soulsearching.elementpage.artistpage.domain

import java.util.UUID

/**
 * Events of the selected artist screen.
 */
sealed interface SelectedArtistEvent {
    data class SetSelectedArtist(val artistId: UUID): SelectedArtistEvent

    data class SetDeleteMusicDialogVisibility(val isShown: Boolean): SelectedArtistEvent

    data class SetMusicBottomSheetVisibility(val isShown: Boolean): SelectedArtistEvent

    data class SetAddToPlaylistBottomSheetVisibility(val isShown: Boolean): SelectedArtistEvent

    data class DeleteMusic(val musicId: UUID): SelectedArtistEvent

    data class ToggleQuickAccessState(val musicId: UUID): SelectedArtistEvent

    data class AddMusicToPlaylists(val musicId: UUID, val selectedPlaylistsIds: List<UUID>):
        SelectedArtistEvent

    data class AddNbPlayed(val artistId: UUID): SelectedArtistEvent
}

package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Music
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

    data class ToggleQuickAccessState(val music: Music): SelectedArtistEvent

    data class AddMusicToPlaylists(val musicId: UUID, val selectedPlaylistsIds: List<UUID>):
        SelectedArtistEvent

    data class AddNbPlayed(val artistId: UUID): SelectedArtistEvent

    data class DeleteAlbum(val albumId: UUID): SelectedArtistEvent

    data class ToggleAlbumQuickAccessState(val album: Album): SelectedArtistEvent
    data class SetDeleteAlbumDialogVisibility(val isShown: Boolean): SelectedArtistEvent

    data class SetAlbumBottomSheetVisibility(val isShown: Boolean): SelectedArtistEvent
}

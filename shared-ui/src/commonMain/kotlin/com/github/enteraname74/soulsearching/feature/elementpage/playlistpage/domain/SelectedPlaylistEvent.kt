package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Events of the selected playlist screen.
 */
sealed interface SelectedPlaylistEvent {
    data class SetSelectedPlaylist(val playlistId: UUID): SelectedPlaylistEvent

    data class SetDeleteMusicDialogVisibility(val isShown: Boolean): SelectedPlaylistEvent

    data class SetMusicBottomSheetVisibility(val isShown: Boolean): SelectedPlaylistEvent

    data class SetAddToPlaylistBottomSheetVisibility(val isShown: Boolean): SelectedPlaylistEvent
    data class SetRemoveFromPlaylistDialogVisibility(val isShown: Boolean): SelectedPlaylistEvent

    data class DeleteMusic(val musicId: UUID): SelectedPlaylistEvent

    data class ToggleQuickAccessState(val music: Music): SelectedPlaylistEvent

    data class AddMusicToPlaylists(val musicId: UUID, val selectedPlaylistsIds: List<UUID>): SelectedPlaylistEvent

    data class AddNbPlayed(val playlistId: UUID): SelectedPlaylistEvent
    data class RemoveMusicFromPlaylist(val playlistId: UUID, val musicId: UUID): SelectedPlaylistEvent
}
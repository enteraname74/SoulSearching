package com.github.soulsearching.elementpage.playlistpage.domain

import java.util.UUID

/**
 * Events of the selected playlist screen.
 */
sealed interface SelectedPlaylistEvent {
    data class SetSelectedPlaylist(val playlistId: UUID): SelectedPlaylistEvent

    data class OpenMusicBottomSheet(val musicId: UUID): SelectedPlaylistEvent


}
package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain

import java.util.*

/**
 * Events of the selected playlist screen.
 */
sealed interface SelectedPlaylistEvent {
    data class SetSelectedPlaylist(val playlistId: UUID): SelectedPlaylistEvent
    data class AddNbPlayed(val playlistId: UUID): SelectedPlaylistEvent
}
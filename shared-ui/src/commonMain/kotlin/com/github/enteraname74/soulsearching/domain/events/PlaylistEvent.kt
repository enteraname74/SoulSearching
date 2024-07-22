package com.github.enteraname74.soulsearching.domain.events

import com.github.enteraname74.domain.model.Playlist
import java.util.UUID

/**
 * Events related to playlists
 */
sealed interface PlaylistEvent {
    data class SetSortDirection(val type: Int) : PlaylistEvent
    data class SetSortType(val type: Int) : PlaylistEvent
    data class AddPlaylist(val name : String) : PlaylistEvent
    data class CreateFavoritePlaylist(val name : String) : PlaylistEvent
    data class SetSelectedPlaylist(val playlist: Playlist) : PlaylistEvent
    data class AddNbPlayed(val playlistId: UUID): PlaylistEvent
}
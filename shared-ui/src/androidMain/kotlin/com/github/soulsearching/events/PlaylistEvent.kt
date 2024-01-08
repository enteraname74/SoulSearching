package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.enteraname74.domain.model.Playlist
import java.util.UUID

/**
 * Events related to playlists
 */
sealed interface PlaylistEvent {
    data object UpdatePlaylist : PlaylistEvent
    data object DeletePlaylist : PlaylistEvent
    data object UpdateQuickAccessState: PlaylistEvent
    data class SetSortDirection(val type: Int) : PlaylistEvent
    data class SetSortType(val type: Int) : PlaylistEvent
    data class AddPlaylist(val name : String) : PlaylistEvent
    data class CreateFavoritePlaylist(val name : String) : PlaylistEvent
    data class SetSelectedPlaylist(val playlist: Playlist) : PlaylistEvent
    data class TogglePlaylistSelectedState(val playlistId: UUID) : PlaylistEvent
    data class PlaylistFromId(val playlistId: UUID) : PlaylistEvent
    data class SetCover(val cover: Bitmap) : PlaylistEvent
    data class SetName(val name: String) : PlaylistEvent
    data class PlaylistsSelection(val musicId: UUID) : PlaylistEvent
    data class AddMusicToPlaylists(val musicId: UUID) : PlaylistEvent
    data class RemoveMusicFromPlaylist(val musicId: UUID) : PlaylistEvent
    data class BottomSheet(val isShown: Boolean) : PlaylistEvent
    data class DeleteDialog(val isShown: Boolean) : PlaylistEvent
    data class CreatePlaylistDialog(val isShown: Boolean) : PlaylistEvent
    data class AddNbPlayed(val playlistId: UUID): PlaylistEvent

}
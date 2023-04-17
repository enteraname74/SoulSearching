package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.Playlist
import java.util.UUID

interface PlaylistEvent {
    object AddPlaylist : PlaylistEvent
    object UpdatePlaylist : PlaylistEvent
    data class DeletePlaylist(val playlist: Playlist) : PlaylistEvent
    data class SetSelectedPlaylist(val playlist: Playlist) : PlaylistEvent
    data class TogglePlaylistSelectedState(val playlistId: UUID) : PlaylistEvent
    data class PlaylistFromId(val playlistId: UUID) : PlaylistEvent
    data class SetCover(val cover: Bitmap) : PlaylistEvent
    data class SetName(val name: String) : PlaylistEvent
    data class PlaylistsSelection(val musicId: UUID) : PlaylistEvent
    data class AddMusicToPlaylists(val musicId: UUID) : PlaylistEvent
}
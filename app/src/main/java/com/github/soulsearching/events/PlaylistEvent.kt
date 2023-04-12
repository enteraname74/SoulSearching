package com.github.soulsearching.events

import com.github.soulsearching.database.model.Playlist

interface PlaylistEvent {
    object AddPlaylist: PlaylistEvent
    data class DeletePlaylist(val playlist : Playlist): PlaylistEvent
    data class SetSelectedPlaylist(val playlist: Playlist) : PlaylistEvent
}
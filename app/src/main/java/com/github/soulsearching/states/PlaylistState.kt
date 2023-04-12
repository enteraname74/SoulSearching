package com.github.soulsearching.states

import com.github.soulsearching.database.model.Playlist

data class PlaylistState(
    val playlists: List<Playlist> = emptyList(),
    var selectedPlaylist : Playlist? = null
)
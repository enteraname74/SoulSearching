package com.github.soulsearching.states

import com.github.soulsearching.database.model.PlaylistWithMusics

/**
 * State for managing a selected playlist.
 */
data class SelectedPlaylistState(
    val playlistWithMusics: PlaylistWithMusics? = PlaylistWithMusics()
)
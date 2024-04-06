package com.github.soulsearching.elementpage.playlistpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics

/**
 * State for managing a selected playlist.
 */
data class SelectedPlaylistState(
    val playlistWithMusics: PlaylistWithMusics? = PlaylistWithMusics()
)
package com.github.soulsearching.states

import com.github.soulsearching.database.model.PlaylistWithMusics

data class SelectedPlaylistState(
    val playlistWithMusics: PlaylistWithMusics? = null
)
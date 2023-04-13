package com.github.soulsearching.states

import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.database.model.PlaylistWithMusics
import java.util.*

data class SelectedPlaylistState(
    val playlistWithMusics: PlaylistWithMusics = PlaylistWithMusics()
)
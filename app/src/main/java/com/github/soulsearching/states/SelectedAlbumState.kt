package com.github.soulsearching.states

import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithMusics
import java.util.*

data class SelectedAlbumState(
    val albumWithMusics : AlbumWithMusics = AlbumWithMusics()
)
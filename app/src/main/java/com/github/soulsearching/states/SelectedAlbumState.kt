package com.github.soulsearching.states

import com.github.soulsearching.database.model.AlbumWithMusics

data class SelectedAlbumState(
    val albumWithMusics : AlbumWithMusics = AlbumWithMusics()
)
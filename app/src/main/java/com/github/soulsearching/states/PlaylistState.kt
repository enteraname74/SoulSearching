package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Playlist

data class PlaylistState(
    val playlists: List<Playlist> = emptyList(),
    val selectedPlaylist : Playlist = Playlist(),
    val multiplePlaylistSelected : List<Playlist> = emptyList(),
    val cover : Bitmap? = null,
    val name : String = ""
)
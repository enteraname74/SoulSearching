package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.database.model.PlaylistWithMusics
import java.util.*
import kotlin.collections.ArrayList

data class PlaylistState(
    val playlists: List<Playlist> = emptyList(),
    val selectedPlaylist : Playlist = Playlist(),
    val playlistsWithoutMusicId : List<PlaylistWithMusics> = emptyList(),
    val multiplePlaylistSelected : ArrayList<UUID> = ArrayList(),
    val cover : Bitmap? = null,
    val name : String = ""
)
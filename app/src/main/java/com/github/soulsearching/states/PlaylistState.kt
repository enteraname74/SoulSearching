package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.classes.SortDirection
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.database.model.PlaylistWithMusics
import com.github.soulsearching.database.model.PlaylistWithMusicsNumber
import java.util.*
import kotlin.collections.ArrayList

data class PlaylistState(
    val playlists: List<PlaylistWithMusicsNumber> = emptyList(),
    val selectedPlaylist: Playlist = Playlist(),
    val playlistsWithMusics: List<PlaylistWithMusics> = emptyList(),
    val multiplePlaylistSelected: ArrayList<UUID> = ArrayList(),
    val cover: Bitmap? = null,
    val name: String = "",
    val isDeleteDialogShown: Boolean = false,
    val isBottomSheetShown: Boolean = false,
    val hasSetNewCover: Boolean = false,
    val isCreatePlaylistDialogShown: Boolean = false,
    var sortType: Int = SortType.NAME,
    var sortDirection: Int = SortDirection.ASC
)
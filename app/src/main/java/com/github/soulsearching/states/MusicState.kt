package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.classes.SortDirection
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.MusicWithCover
import java.util.UUID

data class MusicState(
    val musics: ArrayList<Music> = ArrayList(),
    val musicsWithCover: List<MusicWithCover> = emptyList(),
    val isBottomSheetShown: Boolean = false,
    val isDeleteDialogShown: Boolean = false,
    val isRemoveFromPlaylistDialogShown: Boolean = false,
    val isAddToPlaylistBottomSheetShown: Boolean = false,
    val selectedMusic: Music = Music(),
    var name: String = "",
    var artist: String = "",
    var album: String = "",
    var cover: Bitmap? = null,
    var coverId: UUID? = null,
    var sortType: Int = SortType.NAME,
    var sortDirection: Int = SortDirection.ASC,
    var hasCoverBeenChanged: Boolean = false
)
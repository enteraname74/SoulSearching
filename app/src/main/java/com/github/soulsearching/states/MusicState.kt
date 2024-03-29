package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicWithCover
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import java.util.UUID

/**
 * Manage elements related to musics.
 */
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
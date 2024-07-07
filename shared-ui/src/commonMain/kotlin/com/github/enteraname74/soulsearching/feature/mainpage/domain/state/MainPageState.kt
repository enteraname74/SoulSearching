package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.domain.model.MonthMusicList
import com.github.enteraname74.soulsearching.domain.model.MusicFolder
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortDirection
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortType

/**
 * UI State of the main page.
 */
data class MainPageState(
    val musics: ArrayList<Music> = ArrayList(),
    val allPlaylists: List<PlaylistWithMusics> = emptyList(),
    val isBottomSheetShown: Boolean = false,
    val isDeleteDialogShown: Boolean = false,
    val isRemoveFromPlaylistDialogShown: Boolean = false,
    val isAddToPlaylistBottomSheetShown: Boolean = false,
    val selectedMusic: Music = Music(),
    var sortType: Int = SortType.NAME,
    var sortDirection: Int = SortDirection.ASC,
    var hasCoverBeenChanged: Boolean = false,
    var monthMusics: List<MonthMusicList> = emptyList(),
    var folderMusics: List<MusicFolder> = emptyList()
)
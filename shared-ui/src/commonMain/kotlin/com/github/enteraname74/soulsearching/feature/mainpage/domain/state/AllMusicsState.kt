package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.domain.model.MonthMusicList
import com.github.enteraname74.soulsearching.domain.model.MusicFolder
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType

/**
 * UI State of the all musics tab on the main page.
 */
data class AllMusicsState(
    val musics: List<Music>,
    val allPlaylists: List<PlaylistWithMusics> ,
    var sortType: Int,
    var sortDirection: Int = SortDirection.ASC,
    var monthMusics: List<MonthMusicList>,
    var folderMusics: List<MusicFolder>,
) {
    constructor(): this(
        musics = listOf(),
        allPlaylists = listOf(),
        folderMusics = listOf(),
        sortType = SortType.NAME,
        sortDirection = SortDirection.ASC,
        monthMusics = listOf(),
    )
}
package com.github.enteraname74.soulsearching.domain.ext

import com.github.enteraname74.soulsearching.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.domain.model.Cover

fun List<AlbumWithMusics>.gridCoverWithEmpty(): Cover.Grid? {
    val availableCovers = distinctBy { it.cover }
        .mapNotNull { album ->
            album.cover.takeIf { it?.isEmpty() == false }
        }

    return if (availableCovers.isEmpty()) {
        null
    } else {
        Cover.Grid(availableCovers)
    }
}
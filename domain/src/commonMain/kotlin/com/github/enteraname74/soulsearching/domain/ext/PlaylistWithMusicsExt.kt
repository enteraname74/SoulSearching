package com.github.enteraname74.soulsearching.domain.ext

import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.PlaylistWithMusics

fun List<PlaylistWithMusics>.gridCoverWithEmpty(): Cover.Grid? {
    val availableCovers = distinctBy { it.cover }
        .mapNotNull { artist ->
            artist.cover.takeIf { it?.isEmpty() == false }
        }

    return if (availableCovers.isEmpty()) {
        null
    } else {
        Cover.Grid(availableCovers)
    }
}
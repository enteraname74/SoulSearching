package com.github.enteraname74.soulsearching.domain.ext

import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.Cover

fun List<CollectionWithMusics>.gridCoverWithEmpty(): Cover.Grid? {
    val availableCovers = distinctBy { it.cover }
        .mapNotNull { collection ->
            collection.cover.takeIf { it?.isEmpty() == false }
        }

    return if (availableCovers.isEmpty()) {
        null
    } else {
        Cover.Grid(availableCovers)
    }
}
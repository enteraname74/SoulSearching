package com.github.enteraname74.soulsearching.domain.ext

import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.MusicFolderPreview

fun List<MusicFolderPreview>.gridCoverWithEmpty(): Cover.Grid? {
    val availableCovers = distinctBy { it.cover }
        .mapNotNull { collection ->
            collection.cover.takeIf { !it.isEmpty() }
        }

    return if (availableCovers.isEmpty()) {
        null
    } else {
        Cover.Grid(availableCovers)
    }
}
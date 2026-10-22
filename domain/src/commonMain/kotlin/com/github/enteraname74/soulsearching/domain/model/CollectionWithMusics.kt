package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.domain.ext.coverFromSongs
import com.github.enteraname74.soulsearching.domain.ext.gridCoverIfPossible

data class CollectionWithMusics(
    val collection: Collection,
    val musics: List<Music>,
) {
    val cover: Cover? = when {
        collection.cover?.isEmpty() != false -> musics.gridCoverIfPossible()
        collection.cover is Cover.Simple -> collection.cover.copyIfUrl { it.copy(fallback = musics.coverFromSongs()) }
        else -> collection.cover
    }
}

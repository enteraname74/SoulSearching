package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.domain.ext.coverFromSongs

data class CollectionWithMusics(
    val collection: Collection,
    val musics: List<Music>,
) {
    val cover: Cover? = if (collection.cover?.isEmpty() == false) {
        collection.cover.copyIfUrl { it.copy(fallback = musics.coverFromSongs()) }
    } else {
        musics.coverFromSongs()
    }
}

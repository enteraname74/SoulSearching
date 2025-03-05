package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.ext.coverFromSongs
import java.util.UUID

/**
 * Represent a list of music from a given month
 */
data class MonthMusicList(
    val month: String = "",
    val musics: List<Music> = emptyList(),
) {
    val cover: Cover? = musics.coverFromSongs()
}

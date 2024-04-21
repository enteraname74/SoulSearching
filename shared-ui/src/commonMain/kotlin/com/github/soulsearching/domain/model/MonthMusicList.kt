package com.github.soulsearching.domain.model

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Represent a list of music from a given month
 */
data class MonthMusicList(
    val month: String,
    val musics: List<Music>,
    val coverId: UUID?
)

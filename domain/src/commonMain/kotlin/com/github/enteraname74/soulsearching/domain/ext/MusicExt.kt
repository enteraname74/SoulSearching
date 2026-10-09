package com.github.enteraname74.soulsearching.domain.ext

import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.Music
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun List<Music>.coverFromSongs(): Cover.Simple? =
    this.firstOrNull { !it.cover.isEmpty() }?.cover

fun List<Music>.gridCoverIfPossible(): Cover? {
    val availableCovers = distinctBy { it.cover }
        .mapNotNull { music ->
            music.cover.takeIf { !it.isEmpty() }
        }

    return if (availableCovers.size < 4) {
        coverFromSongs()
    } else {
        Cover.Grid(availableCovers)
    }
}

fun List<Music>.duration(): Duration =
    this.fold(initial = 0L) { current, music ->
        current + music.duration
    }.toDuration(DurationUnit.MILLISECONDS)

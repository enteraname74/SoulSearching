package com.github.enteraname74.domain.ext

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun List<Music>.coverFromSongs(): Cover? =
    this.firstOrNull { !it.cover.isEmpty() }?.cover

fun List<Music>.duration(): Duration =
    this.fold(initial = 0L) { current, music ->
        current + music.duration
    }.toDuration(DurationUnit.MILLISECONDS)
package com.github.enteraname74.domain.ext

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music

fun List<Music>.coverFromSongs(): Cover? =
    this.filter { !it.cover.isEmpty() }.firstOrNull()?.cover
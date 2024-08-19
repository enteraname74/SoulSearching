package com.github.enteraname74.domain.model

import java.util.*

data class MusicFolder(
    val path: String,
    val coverId: UUID?,
    val allMusicsSize: Int,
)

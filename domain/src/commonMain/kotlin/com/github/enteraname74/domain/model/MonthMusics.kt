package com.github.enteraname74.domain.model

import java.util.UUID

data class MonthMusics(
    val month: String,
    val coverId: UUID?,
    val allMusicsSize: Int,
)

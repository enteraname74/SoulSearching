package com.github.enteraname74.domain.model

import java.util.UUID

sealed interface Cover {

    fun isEmpty(): Boolean

    data class CoverFile(
        val initialCoverPath: String? = null,
        val fileCoverId: UUID? = null,
    ): Cover {
        override fun isEmpty(): Boolean =
            initialCoverPath == null && fileCoverId == null
    }
}
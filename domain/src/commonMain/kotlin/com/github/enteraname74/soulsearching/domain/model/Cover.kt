package com.github.enteraname74.soulsearching.domain.model

import kotlin.uuid.Uuid

sealed interface Cover {
    sealed interface Simple : Cover {
        fun copyIfUrl(block: (Url) -> Simple): Simple =
            (this as? Url)?.let { block(this) } ?: this
    }

    fun isEmpty(): Boolean

    fun <T> ifCoverFile(block: (CoverFile) -> T): T? =
        (this as? CoverFile)?.let { block(this) }

    data class CoverFile(
        val initialCoverPath: String? = null,
        val fileCoverId: Uuid? = null,
        val devicePathSpec: DevicePathSpec? = null,
    ) : Simple {
        override fun isEmpty(): Boolean =
            initialCoverPath == null && fileCoverId == null && devicePathSpec == null

        data class DevicePathSpec(
            val settingsKey: String,
            val dynamicElementName: String,
            val fallback: Simple?,
        )
    }

    data class Url(
        val url: String,
        val fallback: Simple?,
    ) : Simple {
        override fun isEmpty(): Boolean = url.isBlank()
    }

    data class Grid(
        val topStart: Simple?,
        val topEnd: Simple?,
        val bottomStart: Simple?,
        val bottomEnd: Simple?,
    ) : Cover {
        val list: List<Simple?> = listOf(
            topStart,
            topEnd,
            bottomStart,
            bottomEnd,
        )

        constructor(covers: List<Simple>) : this(
            topStart = covers.getOrNull(0),
            topEnd = covers.getOrNull(1),
            bottomStart = covers.getOrNull(2),
            bottomEnd = covers.getOrNull(3),
        )

        override fun isEmpty(): Boolean =
            list.none { it?.isEmpty() == false }

        companion object {
            const val GRID_SIZE: Int = 4
        }
    }
}

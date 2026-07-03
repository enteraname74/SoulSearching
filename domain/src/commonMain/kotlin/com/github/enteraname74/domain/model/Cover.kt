package com.github.enteraname74.domain.model

import kotlin.uuid.Uuid

sealed interface Cover {

    fun isEmpty(): Boolean

    fun <T>ifCoverFile(block: (CoverFile) -> T) : T? =
        (this as? CoverFile)?.let { block(this) }

    data class CoverFile(
        val initialCoverPath: String? = null,
        val fileCoverId: Uuid? = null,
        val devicePathSpec: DevicePathSpec? = null,
    ): Cover {
        override fun isEmpty(): Boolean =
            initialCoverPath == null && fileCoverId == null && devicePathSpec == null

        data class DevicePathSpec(
            val settingsKey: String,
            val dynamicElementName: String,
            val fallback: Cover?,
        )
    }

    data class Url(
        val url: String,
    ) : Cover {
        override fun isEmpty(): Boolean = url.isBlank()
    }
}

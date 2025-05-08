package com.github.enteraname74.domain.model

import java.util.UUID

sealed interface Cover {

    fun isEmpty(): Boolean

    data class CoverFile(
        val initialCoverPath: String? = null,
        val fileCoverId: UUID? = null,
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
}
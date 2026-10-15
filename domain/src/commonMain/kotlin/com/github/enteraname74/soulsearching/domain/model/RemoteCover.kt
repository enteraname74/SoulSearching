package com.github.enteraname74.soulsearching.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CoverMode {
    @SerialName("simple")
    Simple,

    @SerialName("grid")
    Grid,
}

fun Cover?.toCoverMode(): CoverMode? = when (this) {
    null -> null
    is Cover.Simple -> CoverMode.Simple
    is Cover.Grid -> CoverMode.Grid
}

@Serializable
sealed interface RemoteCover {
    fun toCover(): Cover

    @Serializable
    @SerialName("simple")
    data class Simple(
        val path: String,
    ) : RemoteCover {
        override fun toCover(): Cover = Cover.Url(path, null)
    }

    @Serializable
    @SerialName("grid")
    data class Grid(
        val topStart: String?,
        val topEnd: String?,
        val bottomStart: String?,
        val bottomEnd: String?,
    ) : RemoteCover {
        override fun toCover(): Cover = Cover.Grid(
            topStart = topStart?.let { Cover.Url(it, null) },
            topEnd = topEnd?.let { Cover.Url(it, null) },
            bottomStart = bottomStart?.let { Cover.Url(it, null) },
            bottomEnd = bottomEnd?.let { Cover.Url(it, null) },
        )
    }
}

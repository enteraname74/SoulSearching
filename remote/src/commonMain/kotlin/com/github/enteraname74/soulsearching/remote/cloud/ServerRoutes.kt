package com.github.enteraname74.soulsearching.remote.cloud

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.util.UUID

object ServerRoutes : KoinComponent {
    private val cloudLocalDataSource: CloudLocalDataSource by inject()
    val HOST: String
        get() = cloudLocalDataSource.getHost()

    object Auth : Route {
        override val END_POINT = "/auth"

        val SIGN_IN: String
            get() = "$ROUTE/sign"
        val LOG_IN: String
            get() = "$ROUTE/login"
        val REFRESH_TOKENS: String
            get() = "$ROUTE/refreshTokens"
    }

    object Music : Route {
        override val END_POINT = "/music"

        val CHECK: String
            get() = "$ROUTE/check"

        val DELETE: String
            get() = ROUTE

        val UPDATE: String
            get() = ROUTE

        fun upload(
            searchMetadata: Boolean
        ): String = "$ROUTE/upload?searchMetadata=$searchMetadata"
    }

    object Album : Route {
        override val END_POINT = "/album"

        val CHECK: String
            get() = "$ROUTE/check"

        val DELETE: String
            get() = ROUTE

        val UPDATE: String
            get() = ROUTE
    }

    object Artist : Route {
        override val END_POINT = "/artist"

        val CHECK: String
            get() = "$ROUTE/check"

        val DELETE: String
            get() = ROUTE

        val UPDATE: String
            get() = ROUTE
    }

    object Playlist : Route {
        override val END_POINT = "/playlist"

        val CHECK: String
            get() = "$ROUTE/check"

        val DELETE: String
            get() = ROUTE

        val UPLOAD: String
            get() = "$ROUTE/upload"

        val UPDATE: String
            get() = ROUTE

        val CREATE: String
            get() = ROUTE

        fun addMusicsToPlaylist(playlistId: UUID): String =
            "$ROUTE/addMusics/$playlistId"

        fun removeMusicsFromPlaylist(playlistId: UUID): String =
            "$ROUTE/removeMusics/$playlistId"
    }

    object MusicArtist : Route {
        override val END_POINT = "/musicartist"
    }

    object MusicPlaylist : Route {
        override val END_POINT = "/musicplaylist"
    }

    private interface Route {
        val END_POINT: String

        val ROUTE: String
            get() = "$HOST$END_POINT"

        fun all(
            after: LocalDateTime?,
            maxPerPage: Int,
            page: Int,
        ): String {
            val afterText: String = after?.let {
                "lastUpdateAt=$it&"
            } ?: ""

            return "$ROUTE/ofUser?${afterText}maxPerPage=$maxPerPage&page=$page"
        }
    }
}

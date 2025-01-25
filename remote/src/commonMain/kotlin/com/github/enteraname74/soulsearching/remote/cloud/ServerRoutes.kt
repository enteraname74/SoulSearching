package com.github.enteraname74.soulsearching.remote.cloud

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime

object ServerRoutes : KoinComponent {
    private val cloudLocalDataSource: CloudLocalDataSource by inject()
    private val HOST: String
        get() = cloudLocalDataSource.getHost()

    object Auth : Route {
        override val BASE_ROUTE = "/auth"

        val SIGN_IN: String
            get() = "$HOST$BASE_ROUTE/sign"
        val LOG_IN: String
            get() = "$HOST$BASE_ROUTE/login"
        val REFRESH_TOKENS: String
            get() = "$HOST$BASE_ROUTE/refreshTokens"
    }

    object Music : Route {
        override val BASE_ROUTE = "/music"

        val CHECK: String
            get() = "$HOST$BASE_ROUTE/check"

        val DELETE: String
            get() = "$HOST$BASE_ROUTE"

        fun upload(
            searchMetadata: Boolean
        ): String = "$HOST$BASE_ROUTE/upload?searchMetadata=$searchMetadata"

        fun all(
            after: LocalDateTime?,
            maxPerPage: Int,
            page: Int,
        ): String {
            val afterText: String = after?.let {
                "lastUpdateAt=$it&"
            } ?: ""

            return "$HOST$BASE_ROUTE/ofUser?${afterText}maxPerPage=$maxPerPage&page=$page"
        }
    }

    object Album : Route {
        override val BASE_ROUTE = "/album"

        val CHECK: String
            get() = "$HOST$BASE_ROUTE/check"

        val DELETE: String
            get() = "$HOST${BASE_ROUTE}"

        fun all(
            after: LocalDateTime?,
            maxPerPage: Int,
            page: Int,
        ): String {
            val afterText: String = after?.let {
                "lastUpdateAt=$it&"
            } ?: ""

            return "$HOST$BASE_ROUTE/ofUser?${afterText}maxPerPage=$maxPerPage&page=$page"
        }
    }

    object Artist : Route {
        override val BASE_ROUTE = "/artist"

        val CHECK: String
            get() = "$HOST$BASE_ROUTE/check"

        val DELETE: String
            get() = "$HOST${BASE_ROUTE}"

        fun all(
            after: LocalDateTime?,
            maxPerPage: Int,
            page: Int,
        ): String {
            val afterText: String = after?.let {
                "lastUpdateAt=$it&"
            } ?: ""

            return "$HOST$BASE_ROUTE/ofUser?${afterText}maxPerPage=$maxPerPage&page=$page"
        }
    }

    object MusicArtist : Route {
        override val BASE_ROUTE = "/musicartist"

        fun all(
            after: LocalDateTime?,
            maxPerPage: Int,
            page: Int,
        ): String {
            val afterText: String = after?.let {
                "lastUpdateAt=$it&"
            } ?: ""

            return "$HOST${BASE_ROUTE}/ofUser?${afterText}maxPerPage=$maxPerPage&page=$page"
        }
    }

    private interface Route {
        val BASE_ROUTE: String
    }
}

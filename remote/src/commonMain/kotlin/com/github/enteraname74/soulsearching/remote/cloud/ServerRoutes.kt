package com.github.enteraname74.soulsearching.remote.cloud

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
    }

    object Music : Route {
        override val BASE_ROUTE = "/music"

        val ALL: String
            get() = "$HOST$BASE_ROUTE/ofUser"
    }

    private interface Route {
        val BASE_ROUTE: String
    }
}

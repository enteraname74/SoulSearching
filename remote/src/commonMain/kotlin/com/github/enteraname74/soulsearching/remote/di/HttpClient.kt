package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.UserTokens
import com.github.enteraname74.domain.util.LocaleUtils
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds


internal expect fun createPlatformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient

private fun <T : HttpClientEngineConfig> HttpClientConfig<T>.setLanguage() {
    defaultRequest {
        header(HttpHeaders.AcceptLanguage, LocaleUtils.currentLanguage())
    }
}
fun provideHttpClient(): HttpClient =
    createPlatformHttpClient {
        install(Resources)
        setLanguage()
        installContentNegotiation()
    }

fun <T : HttpClientEngineConfig> HttpClientConfig<T>.installContentNegotiation() {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
}

fun provideCloudHttpClient(
    userLocalDataSource: UserLocalDataSource,
    userRemoteDataSource: UserRemoteDataSource,
): HttpClient =
    createPlatformHttpClient {
        install(Resources)
        setLanguage()
        installContentNegotiation()

        install(Auth) {
            bearer {
                loadTokens {
                    val user = userLocalDataSource.observeUser().firstOrNull()
                    BearerTokens(
                        accessToken = user?.accessToken.orEmpty(),
                        refreshToken = user?.refreshToken.orEmpty(),
                    )
                }

                refreshTokens {
                    val user = userLocalDataSource.observeUser().firstOrNull()
                    val result: SoulResult<UserTokens> = userRemoteDataSource.refreshTokens()
                    val tokens = (result as? SoulResult.Success)?.data
                    tokens?.let { tokens ->
                        user?.let { user ->
                            userLocalDataSource.upsert(
                                user = user.copy(
                                    accessToken = tokens.accessToken,
                                    refreshToken = tokens.refreshToken
                                )
                            )
                        }
                    }
                    BearerTokens(
                        accessToken = tokens?.accessToken.orEmpty(),
                        refreshToken = tokens?.refreshToken.orEmpty(),
                    )
                }
            }
        }

        install(WebSockets) {
            pingInterval = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

object HttpClientNames {
    val CLOUD: String = "CLOUD"
}

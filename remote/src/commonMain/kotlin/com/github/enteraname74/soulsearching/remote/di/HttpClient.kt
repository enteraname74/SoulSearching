package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.domain.AppVersion
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.UserTokens
import com.github.enteraname74.domain.util.LocaleUtils
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

internal expect fun createPlatformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient

private fun <T : HttpClientEngineConfig> HttpClientConfig<T>.defaultRequestHeaders() {
    defaultRequest {
        commonRequestHeaders()
    }
}

internal fun DefaultRequest.DefaultRequestBuilder.commonRequestHeaders() {
    headers.appendIfNameAbsent(HttpHeaders.AcceptLanguage, LocaleUtils.currentLanguage())
    headers.appendIfNameAbsent(APP_VERSION_HEADER, AppVersion.versionName)
}

fun provideHttpClient(): HttpClient =
    createPlatformHttpClient {
        install(Resources)
        defaultRequestHeaders()
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
        defaultRequestHeaders()
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
                    val refreshToken = oldTokens
                        ?.refreshToken
                        ?.takeIf(String::isNotBlank)
                        ?: return@refreshTokens null
                    val user = userLocalDataSource.observeUser().firstOrNull()
                    val result: SoulResult<UserTokens> = userRemoteDataSource.refreshTokens(
                        refreshToken = refreshToken,
                    )
                    val tokens = (result as? SoulResult.Success)?.data

                    if (tokens == null || user == null) {
                        null
                    } else {
                        userLocalDataSource.upsert(
                            user = user.copy(
                                accessToken = tokens.accessToken,
                                refreshToken = tokens.refreshToken
                            )
                        )
                        BearerTokens(
                            accessToken = tokens.accessToken,
                            refreshToken = tokens.refreshToken,
                        )
                    }
                }
            }
        }

        install(WebSockets) {
            pingInterval = 15.seconds
            maxFrameSize = PLAYER_WEBSOCKET_MAX_FRAME_SIZE
            contentConverter = KotlinxWebsocketSerializationConverter(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

object HttpClientNames {
    const val CLOUD: String = "CLOUD"
}

internal const val APP_VERSION_HEADER: String = "SoulSearching-version"
private const val PLAYER_WEBSOCKET_MAX_FRAME_SIZE: Long = 4 * 1024L

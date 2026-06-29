package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.UserTokens
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

fun provideHttpClient(): HttpClient =
    HttpClient(CIO) {
        install(Resources)
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
    HttpClient(CIO) {
        install(Resources)
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
    }

object HttpClientNames {
    val CLOUD: String = "CLOUD"
}
package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.soulsearching.remote.cloud.CloudLocalDataSource
import com.github.enteraname74.soulsearching.remote.datasourceimpl.AuthRemoteDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.LyricsDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.MusicRemoteDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.ReleaseDataSourceImpl
import com.github.enteraname74.soulsearching.remote.model.HttpClientNames
import com.github.enteraname74.soulsearching.repository.datasource.auth.AuthRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.LyricsDataSource
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteModule = module {
    single(named(HttpClientNames.GENERIC)) {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single(named(HttpClientNames.CLOUD)) {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(Auth) {
                val cloudLocalDataSource = get<CloudLocalDataSource>()
                val authRemoteDataSource = get<AuthRemoteDataSource>()

                bearer {
                    loadTokens {
                        val token = cloudLocalDataSource.getToken()

                        BearerTokens(
                            accessToken = token,
                            refreshToken = token,
                        )
                    }

                    refreshTokens {
                        val currentUser = cloudLocalDataSource.getUser()

                        authRemoteDataSource.logIn(currentUser)
                        val updatedToken = cloudLocalDataSource.getToken()

                        BearerTokens(
                            accessToken = updatedToken,
                            refreshToken = updatedToken,
                        )
                    }
                }
            }
        }
    }

    single<LyricsDataSource> {
        LyricsDataSourceImpl(
            client = get(named(HttpClientNames.GENERIC))
        )
    }
    single<ReleaseDataSource> {
        ReleaseDataSourceImpl(
            client = get(named(HttpClientNames.GENERIC))
        )
    }

    single<AuthRemoteDataSource> {
        AuthRemoteDataSourceImpl(
            client = get(named(HttpClientNames.GENERIC)),
            settings = get(),
        )
    }

    single<MusicRemoteDataSource> {
        MusicRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD)),
        )
    }

    singleOf(::CloudLocalDataSource)
}
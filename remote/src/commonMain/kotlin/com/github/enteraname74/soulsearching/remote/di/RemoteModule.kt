package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.cloud.CloudLocalDataSource
import com.github.enteraname74.soulsearching.remote.datasourceimpl.*
import com.github.enteraname74.soulsearching.remote.ext.toBearerTokens
import com.github.enteraname74.soulsearching.remote.model.HttpClientNames
import com.github.enteraname74.soulsearching.remote.model.JSON
import com.github.enteraname74.soulsearching.repository.datasource.LyricsDataSource
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.auth.AuthRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistRemoteDataSource
import com.github.enteraname74.soulsearching.repository.model.UserTokens
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteModule = module {
    single(named(HttpClientNames.GENERIC)) {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(JSON)
            }
        }
    }

    single(named(HttpClientNames.CLOUD)) {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(JSON)
            }

            engine {
                requestTimeout = 0L
            }

            install(Auth) {
                val cloudLocalDataSource = get<CloudLocalDataSource>()
                val authRemoteDataSource = get<AuthRemoteDataSource>()

                bearer {
                    loadTokens {
                        cloudLocalDataSource.getUserToken().toBearerTokens()
                    }

                    refreshTokens {
                        val result: SoulResult<UserTokens> = authRemoteDataSource.refreshTokens()

                        (result as? SoulResult.Success)?.data?.let {
                            cloudLocalDataSource.setUserToken(userToken = it)
                        }
                        cloudLocalDataSource.getUserToken().toBearerTokens()
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

    single<AlbumRemoteDataSource> {
        AlbumRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD)),
        )
    }

    single<ArtistRemoteDataSource> {
        ArtistRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD)),
        )
    }

    single<MusicArtistRemoteDataSource> {
        MusicArtistRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD)),
        )
    }

    singleOf(::CloudLocalDataSource)
}
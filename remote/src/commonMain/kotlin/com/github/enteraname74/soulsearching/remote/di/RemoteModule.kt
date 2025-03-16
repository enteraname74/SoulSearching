package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.cloud.CloudLocalDataSource
import com.github.enteraname74.soulsearching.remote.datasourceimpl.*
import com.github.enteraname74.soulsearching.remote.ext.toBearerTokens
import com.github.enteraname74.soulsearching.remote.model.HttpClientNames
import com.github.enteraname74.soulsearching.repository.datasource.LyricsRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.auth.AuthRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicplaylist.MusicPlaylistRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistRemoteDataSource
import com.github.enteraname74.soulsearching.repository.model.UserTokens
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteModule = module {
    single(named(HttpClientNames.GENERIC)) {
        HttpClient(CIO) {
            provideContentNegotiation()
        }
    }

    single(named(HttpClientNames.CLOUD_AUTH)) {
        HttpClient(CIO) {
            provideContentNegotiation()

            engine {
                requestTimeout = 60_000L
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

    single<LyricsRemoteDataSource> {
        LyricsRemoteDataSourceImpl(
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
            client = get(named(HttpClientNames.CLOUD_AUTH)),
        )
    }

    single<AlbumRemoteDataSource> {
        AlbumRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD_AUTH)),
        )
    }

    single<ArtistRemoteDataSource> {
        ArtistRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD_AUTH)),
        )
    }

    single<MusicArtistRemoteDataSource> {
        MusicArtistRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD_AUTH)),
        )
    }

    single<MusicPlaylistRemoteDataSource> {
        MusicPlaylistRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD_AUTH)),
        )
    }

    single<PlaylistRemoteDataSource> {
        PlaylistRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD_AUTH))
        )
    }

    singleOf(::CloudLocalDataSource)
}
package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.soulsearching.remote.datasourceimpl.CoverRemoteDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.LyricsRemoteDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.MusicRemoteDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.PlayerRemoteDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.ReleaseDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.UserRemoteDataSourceImpl
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import com.github.enteraname74.soulsearching.repository.datasource.cover.CoverRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteModule = module {
    factory { provideHttpClient() }
    single(named(HttpClientNames.CLOUD)) {
        provideCloudHttpClient(
            userLocalDataSource = get(),
            userRemoteDataSource = get(),
        )
    }
    factoryOf(::LyricsRemoteDataSourceImpl) bind LyricsRemoteDataSource::class
    factoryOf(::ReleaseDataSourceImpl) bind ReleaseDataSource::class
    factoryOf(::UserRemoteDataSourceImpl) bind UserRemoteDataSource::class

    factory<PlayerRemoteDataSource> {
        PlayerRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD)),
            cloudPreferencesDataSource = get()
        )
    }

    factory<MusicRemoteDataSource> {
        MusicRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD)),
            cloudPreferencesDataSource = get()
        )
    }

    factory<CoverRemoteDataSource> {
        CoverRemoteDataSourceImpl(
            client = get(named(HttpClientNames.CLOUD)),
            cloudPreferencesDataSource = get()
        )
    }
}
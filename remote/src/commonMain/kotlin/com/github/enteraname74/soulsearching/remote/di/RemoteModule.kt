package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.soulsearching.remote.datasourceimpl.LyricsRemoteDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.ReleaseDataSourceImpl
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteModule = module {
    factory { provideHttpClient() }
    factoryOf(::LyricsRemoteDataSourceImpl) bind LyricsRemoteDataSource::class
    factoryOf(::ReleaseDataSourceImpl) bind ReleaseDataSource::class
}
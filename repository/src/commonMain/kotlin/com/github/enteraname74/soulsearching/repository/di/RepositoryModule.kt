package com.github.enteraname74.soulsearching.repository.di

import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.repository.repositoryimpl.AlbumArtistRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.AlbumRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<AlbumArtistRepository> {
        AlbumArtistRepositoryImpl(
            albumArtistDataSource = get()
        )
    }
}
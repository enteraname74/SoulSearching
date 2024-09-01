package com.github.enteraname74.soulsearching.localdesktop

import com.github.enteraname74.soulsearching.repository.datasource.*
import com.github.enteraname74.soulsearching.localdesktop.dao.*
import com.github.enteraname74.soulsearching.localdesktop.datasourceimpl.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val localDesktopModule: Module = module {
    // DAO
    singleOf(::AlbumArtistDao)
    singleOf(::AlbumDao)
    singleOf(::ArtistDao)
    singleOf(::FolderDao)
    singleOf(::ImageCoverDao)
    singleOf(::MusicAlbumDao)
    singleOf(::MusicArtistDao)
    singleOf(::MusicDao)
    singleOf(::MusicPlaylistDao)
    singleOf(::PlayerMusicDao)
    singleOf(::PlaylistDao)

    // DATA SOURCE IMPL
    singleOf(::AlbumArtistDataSourceImpl) bind AlbumArtistDataSource::class
    singleOf(::AlbumDataSourceImpl) bind AlbumDataSource::class
    singleOf(::ArtistDataSourceImpl) bind ArtistDataSource::class
    singleOf(::FolderDataSourceImpl) bind FolderDataSource::class
    singleOf(::ImageCoverDataSourceImpl) bind ImageCoverDataSource::class
    singleOf(::MusicAlbumDataSourceImpl) bind MusicAlbumDataSource::class
    singleOf(::MusicArtistDataSourceImpl) bind MusicArtistDataSource::class
    singleOf(::MusicDataSourceImpl) bind MusicDataSource::class
    singleOf(::MusicPlaylistDataSourceImpl) bind MusicPlaylistDataSource::class
    singleOf(::PlayerMusicDataSourceImpl) bind PlayerMusicDataSource::class
    singleOf(::PlaylistDataSourceImpl) bind PlaylistDataSource::class
}
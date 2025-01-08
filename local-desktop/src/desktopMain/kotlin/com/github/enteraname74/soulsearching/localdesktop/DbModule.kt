package com.github.enteraname74.soulsearching.localdesktop

import com.github.enteraname74.soulsearching.localdesktop.dao.*
import com.github.enteraname74.soulsearching.localdesktop.datasourceimpl.*
import com.github.enteraname74.soulsearching.repository.datasource.*
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
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
    singleOf(::MusicAlbumDao)
    singleOf(::MusicArtistDao)
    singleOf(::MusicDao)
    singleOf(::MusicPlaylistDao)
    singleOf(::PlayerMusicDao)
    singleOf(::PlaylistDao)

    // DATA SOURCE IMPL
    singleOf(::AlbumArtistDataSourceImpl) bind AlbumArtistDataSource::class
    singleOf(::AlbumLocalDataSourceImpl) bind AlbumLocalDataSource::class
    singleOf(::ArtistLocalDataSourceImpl) bind ArtistLocalDataSource::class
    singleOf(::FolderDataSourceImpl) bind FolderDataSource::class
    singleOf(::MusicAlbumDataSourceImpl) bind MusicAlbumDataSource::class
    singleOf(::MusicArtistDataSourceImpl) bind MusicArtistDataSource::class
    singleOf(::MusicLocalDataSourceImpl) bind MusicLocalDataSource::class
    singleOf(::MusicPlaylistDataSourceImpl) bind MusicPlaylistDataSource::class
    singleOf(::PlayerMusicDataSourceImpl) bind PlayerMusicDataSource::class
    singleOf(::PlaylistDataSourceImpl) bind PlaylistDataSource::class
}
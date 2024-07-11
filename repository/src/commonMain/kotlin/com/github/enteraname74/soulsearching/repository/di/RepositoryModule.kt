package com.github.enteraname74.soulsearching.repository.di

import com.github.enteraname74.domain.repository.*
import com.github.enteraname74.soulsearching.repository.repositoryimpl.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AlbumArtistRepositoryImpl) bind AlbumArtistRepository::class
    singleOf(::AlbumRepositoryImpl) bind AlbumRepository::class
    singleOf(::ArtistRepositoryImpl) bind ArtistRepository::class
    singleOf(::FolderRepositoryImpl) bind FolderRepository::class
    singleOf(::ImageCoverRepositoryImpl) bind ImageCoverRepository::class
    singleOf(::LyricsRepositoryImpl) bind LyricsRepository::class
    singleOf(::MusicAlbumRepositoryImpl) bind MusicAlbumRepository::class
    singleOf(::MusicArtistRepositoryImpl) bind MusicArtistRepository::class
    singleOf(::MusicPlaylistRepositoryImpl) bind MusicPlaylistRepository::class
    singleOf(::MusicRepositoryImpl) bind MusicRepository::class
    singleOf(::PlayerMusicRepositoryImpl) bind PlayerMusicRepository::class
    singleOf(::PlaylistRepositoryImpl) bind PlaylistRepository::class
}
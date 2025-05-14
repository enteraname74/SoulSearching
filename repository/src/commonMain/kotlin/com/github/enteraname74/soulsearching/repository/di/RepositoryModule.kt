package com.github.enteraname74.soulsearching.repository.di

import com.github.enteraname74.domain.repository.*
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsLocalDataSource
import com.github.enteraname74.soulsearching.repository.repositoryimpl.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AlbumRepositoryImpl) bind AlbumRepository::class
    singleOf(::ArtistRepositoryImpl) bind ArtistRepository::class
    singleOf(::FolderRepositoryImpl) bind FolderRepository::class
    singleOf(::CoverRepositoryImpl) bind CoverRepository::class
    singleOf(::LyricsRepositoryImpl) bind LyricsRepository::class
    singleOf(::MusicArtistRepositoryImpl) bind MusicArtistRepository::class
    singleOf(::MusicPlaylistRepositoryImpl) bind MusicPlaylistRepository::class
    singleOf(::MusicRepositoryImpl) bind MusicRepository::class
    singleOf(::PlayerMusicRepositoryImpl) bind PlayerMusicRepository::class
    singleOf(::PlaylistRepositoryImpl) bind PlaylistRepository::class
    singleOf(::ReleaseRepositoryImpl) bind ReleaseRepository::class

    singleOf(::LyricsLocalDataSource)
}
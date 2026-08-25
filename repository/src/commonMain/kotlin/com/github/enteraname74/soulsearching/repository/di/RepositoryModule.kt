package com.github.enteraname74.soulsearching.repository.di

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.CoverRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.domain.repository.LyricsRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.repository.ReleaseRepository
import com.github.enteraname74.domain.repository.UserInscriptionCodeRepository
import com.github.enteraname74.domain.repository.UserRepository
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsLocalDataSource
import com.github.enteraname74.soulsearching.repository.repositoryimpl.AlbumRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.ArtistRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.CloudPreferencesRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.CoverRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.FolderRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.ListeningStatisticsRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.LyricsRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.MusicArtistRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.MusicPlaylistRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.MusicRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.PlayerRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.PlaylistRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.ReleaseRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.UserInscriptionCodeRepositoryImpl
import com.github.enteraname74.soulsearching.repository.repositoryimpl.UserRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule: Module = module {
    singleOf(::AlbumRepositoryImpl) bind AlbumRepository::class
    singleOf(::ArtistRepositoryImpl) bind ArtistRepository::class
    singleOf(::FolderRepositoryImpl) bind FolderRepository::class
    singleOf(::CoverRepositoryImpl) bind CoverRepository::class
    singleOf(::LyricsRepositoryImpl) bind LyricsRepository::class
    singleOf(::MusicArtistRepositoryImpl) bind MusicArtistRepository::class
    singleOf(::MusicPlaylistRepositoryImpl) bind MusicPlaylistRepository::class
    singleOf(::MusicRepositoryImpl) bind MusicRepository::class
    single<PlayerRepository> {
        PlayerRepositoryImpl(
            playerLocalDataSource = get(),
            workScope = get<WorkDispatcher>().dispatcher,
            playerRemoteDataSource = get(),
            deviceLocalDataSource = get(),
            userLocalDataSource = get(),
            musicLocalDataSource = get(),
            settings = get()
        )
    }
    singleOf(::PlaylistRepositoryImpl) bind PlaylistRepository::class
    singleOf(::ReleaseRepositoryImpl) bind ReleaseRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::CloudPreferencesRepositoryImpl) bind CloudPreferencesRepository::class
    singleOf(::LyricsLocalDataSource)
    singleOf(::UserInscriptionCodeRepositoryImpl) bind UserInscriptionCodeRepository::class
    singleOf(::ListeningStatisticsRepositoryImpl) bind ListeningStatisticsRepository::class
}

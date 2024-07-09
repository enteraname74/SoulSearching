package com.github.enteraname74.domain

import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerMusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import com.github.enteraname74.domain.util.LyricsProvider
import org.koin.dsl.module

/**
 * Module for the domain.
 */
val domainModule = module {
    single { AlbumArtistRepository(get()) }
    single {
        AlbumRepository(
            albumDataSource = get(),
            checkAndDeleteVerification = get(),
            musicDataSource = get(),
            artistDataSource = get(),
            albumArtistDataSource = get(),
            musicArtistDataSource = get(),
            musicAlbumDataSource = get(),
            imageCoverDataSource = get()
        )
    }
    single {
        ArtistRepository(
            artistDataSource = get(),
            albumDataSource = get(),
            musicAlbumDataSource = get(),
            musicArtistDataSource = get(),
            musicDataSource = get(),
            albumArtistDataSource = get()
        )
    }
    single { FolderRepository(get()) }
    single { ImageCoverRepository(get()) }
    single { MusicAlbumRepository(get()) }
    single { MusicArtistRepository(get()) }
    single { MusicPlaylistRepository(get()) }
    single {
        MusicRepository(
            musicAlbumDataSource = get(),
            musicArtistDataSource = get(),
            musicDataSource = get(),
            albumArtistDataSource = get(),
            albumDataSource = get(),
            artistDataSource = get(),
            imageCoverDataSource = get(),
            folderDataSource = get(),
            playlistDataSource = get(),
            musicPlaylistDataSource = get(),
            checkAndDeleteVerification = get()
        )
    }
    single { PlayerMusicRepository(get()) }
    single {
        PlaylistRepository(get())
    }
    single {
        CheckAndDeleteVerification(
            albumArtistRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            musicAlbumRepository = get(),
            albumRepository = get()
        )
    }
    single {
        LyricsProvider()
    }
}
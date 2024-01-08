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
import org.koin.dsl.module

/**
 * Module for the domain.
 */
val domainModule = module {
    single { AlbumArtistRepository(get()) }
    single { AlbumRepository(get()) }
    single { ArtistRepository(get()) }
    single { FolderRepository(get()) }
    single { ImageCoverRepository(get()) }
    single { MusicAlbumRepository(get()) }
    single { MusicArtistRepository(get()) }
    single { MusicPlaylistRepository(get()) }
    single { MusicRepository(get()) }
    single { PlayerMusicRepository(get()) }
    single { PlaylistRepository(get()) }
}
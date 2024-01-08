package com.github.enteraname74

import com.github.enteraname74.repository.AlbumArtistRepository
import com.github.enteraname74.repository.AlbumRepository
import com.github.enteraname74.repository.ArtistRepository
import com.github.enteraname74.repository.FolderRepository
import com.github.enteraname74.repository.ImageCoverRepository
import com.github.enteraname74.repository.MusicAlbumRepository
import com.github.enteraname74.repository.MusicArtistRepository
import com.github.enteraname74.repository.MusicPlaylistRepository
import com.github.enteraname74.repository.MusicRepository
import com.github.enteraname74.repository.PlayerMusicRepository
import com.github.enteraname74.repository.PlaylistRepository
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
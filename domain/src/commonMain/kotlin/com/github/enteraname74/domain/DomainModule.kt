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
import com.github.enteraname74.domain.service.AlbumService
import com.github.enteraname74.domain.service.ImageCoverService
import com.github.enteraname74.domain.service.MusicService
import com.github.enteraname74.domain.serviceimpl.AlbumServiceImpl
import com.github.enteraname74.domain.serviceimpl.ImageCoverServiceImpl
import com.github.enteraname74.domain.serviceimpl.MusicServiceImpl
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import org.koin.dsl.module

/**
 * Module for the domain.
 */
val domainModule = module {
    factory { AlbumArtistRepository(get()) }
    factory { AlbumRepository(get()) }
    factory { ArtistRepository(get()) }
    factory { FolderRepository(get()) }
    factory { ImageCoverRepository(get()) }
    factory { MusicAlbumRepository(get()) }
    factory { MusicArtistRepository(get()) }
    factory { MusicPlaylistRepository(get()) }
    factory { MusicRepository(get()) }
    factory { PlayerMusicRepository(get()) }
    factory { PlaylistRepository(get()) }

    factory {
        CheckAndDeleteVerification(
            albumRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            musicAlbumRepository = get(),
            albumArtistRepository = get()
        )
    }

    factory<MusicService> {
        MusicServiceImpl(
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            albumArtistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            imageCoverRepository = get(),
            folderRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            checkAndDeleteVerification = get()
        )
    }

    factory<ImageCoverService> {
        ImageCoverServiceImpl(
            imageCoverRepository = get()
        )
    }

    factory<AlbumService> {
        AlbumServiceImpl(
            musicRepository = get(),
            albumRepository = get(),
            checkAndDeleteVerification = get()
        )
    }
}
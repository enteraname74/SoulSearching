package com.github.soulsearching

import com.github.soulsearching.viewmodel.AddMusicsViewModel
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllFoldersViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.AllQuickAccessViewModel
import com.github.soulsearching.viewmodel.MainActivityViewModel
import com.github.soulsearching.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.viewmodel.ModifyArtistViewModel
import com.github.soulsearching.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.viewmodel.PlayerViewModel
import com.github.soulsearching.viewmodel.SelectedAlbumViewModel
import com.github.soulsearching.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Module for all ViewModel used in the application.
 */
val viewModelModule = module {
    viewModel {
        AddMusicsViewModel(
            folderRepository = get(),
            musicRepository = get(),
            playlistRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        AllAlbumsViewModel(
            albumRepository = get(),
            musicRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get()
        )
    }
    viewModel {
        AllArtistsViewModel(
            artistRepository = get(),
            musicRepository = get(),
            albumRepository = get()
        )
    }
    viewModel {
        AllFoldersViewModel(
            folderRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            albumArtistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get()
        )
    }
    viewModel {
        AllImageCoversViewModel(
            imageCoverRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            playlistRepository = get()
        )
    }
    viewModel {
        AllMusicsViewModel(
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
            folderRepository = get()
        )
    }
    viewModel {
        AllPlaylistsViewModel(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        AllQuickAccessViewModel(
            musicRepository = get(),
            playlistRepository = get(),
            albumRepository = get(),
            artistRepository = get()
        )
    }
    viewModel {
        MainActivityViewModel()
    }
    viewModel {
        ModifyAlbumViewModel(
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            musicAlbumRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        ModifyArtistViewModel(
            musicRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            musicAlbumRepository = get(),
            albumArtistRepository = get(),
            albumRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        ModifyMusicViewModel(
            musicRepository = get(),
            playlistRepository = get(),
            artistRepository = get(),
            albumRepository = get(),
            musicPlaylistRepository = get(),
            musicAlbumRepository = get(),
            albumArtistRepository = get(),
            musicArtistRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        ModifyPlaylistViewModel(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        PlayerMusicListViewModel(
            playerMusicRepository = get(),
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        PlayerViewModel(
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
        )
    }
    viewModel {
        SelectedAlbumViewModel(
            albumRepository = get(),
            artistRepository = get(),
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        SelectedArtistViewModel(
            artistRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            albumArtistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            imageCoverRepository = get()
        )
    }
    viewModel {
        SelectedPlaylistViewModel(
            playlistRepository = get(),
            musicRepository = get(),
            artistRepository = get(),
            albumRepository = get(),
            albumArtistRepository = get(),
            musicPlaylistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            imageCoverRepository = get()
        )
    }
//    viewModel {
//        SettingsViewModel()
//    }
}
package com.github.soulsearching

import android.content.Context
import com.github.soulsearching.classes.utils.MusicFetcherAndroidImpl
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.model.settings.SoulSearchingSettingsImpl
import com.github.soulsearching.model.settings.ViewSettingsManager
import com.github.soulsearching.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllAlbumsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllArtistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllImageCoversViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllPlaylistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllQuickAccessViewModelAndroidImpl
import com.github.soulsearching.viewmodel.MainActivityViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyAlbumViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyArtistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyMusicViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.NavigationViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerMusicListViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedAlbumViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedArtistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModelAndroidImpl
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Module for all ViewModel used in the application.
 */
val androidModule = module {
    viewModel {
        SettingsAddMusicsViewModelAndroidImpl(
            folderRepository = get(),
            musicRepository = get(),
            musicFetcher = get()
        )
    }
    viewModel {
        AllAlbumsViewModelAndroidImpl(
            albumRepository = get(),
            musicRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            settings = get()
        )
    }
    viewModel {
        AllArtistsViewModelAndroidImpl(
            artistRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            settings = get()
        )
    }
    viewModel {
        SettingsAllFoldersViewModelAndroidImpl(
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
        AllImageCoversViewModelAndroidImpl(
            imageCoverRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            playlistRepository = get()
        )
    }
    viewModel {
        AllMusicsViewModelAndroidImpl(
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
            folderRepository = get(),
            settings = get(),
            context = androidContext(),
            playbackManager = get(),
            musicFetcher = get()
        )
    }
    viewModel {
        AllPlaylistsViewModelAndroidImpl(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            imageCoverRepository = get(),
            settings = get()
        )
    }
    viewModel {
        AllQuickAccessViewModelAndroidImpl(
            musicRepository = get(),
            playlistRepository = get(),
            albumRepository = get(),
            artistRepository = get()
        )
    }
    viewModel {
        MainActivityViewModelAndroidImpl(
            settings = get()
        )
    }
    viewModel {
        ModifyAlbumViewModelAndroidImpl(
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            musicAlbumRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
            playbackManager = get()
        )
    }
    viewModel {
        ModifyArtistViewModelAndroidImpl(
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
        ModifyMusicViewModelAndroidImpl(
            musicRepository = get(),
            playlistRepository = get(),
            artistRepository = get(),
            albumRepository = get(),
            musicPlaylistRepository = get(),
            musicAlbumRepository = get(),
            albumArtistRepository = get(),
            musicArtistRepository = get(),
            imageCoverRepository = get(),
            settings = get(),
            playbackManager = get()
        )
    }
    viewModel {
        ModifyPlaylistViewModelAndroidImpl(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            imageCoverRepository = get(),
            settings = get()
        )
    }
    viewModel {
        PlayerMusicListViewModelAndroidImpl(
            playerMusicRepository = get(),
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
            settings = get(),
            playbackManager = get()
        )
    }
    viewModel {
        PlayerViewModelAndroidImpl(
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
            settings = get(),
            playbackManager = get(),
            colorThemeManager = get()
        )
    }
    viewModel {
        SelectedAlbumViewModelAndroidImpl(
            albumRepository = get(),
            artistRepository = get(),
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
            settings = get(),
            playbackManager = get()
        )
    }
    viewModel {
        SelectedArtistViewModelAndroidImpl(
            artistRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            albumArtistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            imageCoverRepository = get(),
            settings = get(),
            playbackManager = get()
        )
    }
    viewModel {
        SelectedPlaylistViewModelAndroidImpl(
            playlistRepository = get(),
            musicRepository = get(),
            artistRepository = get(),
            albumRepository = get(),
            albumArtistRepository = get(),
            musicPlaylistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            imageCoverRepository = get(),
            settings = get(),
            playbackManager = get()
        )
    }
    viewModel {
        NavigationViewModelAndroidImpl()
    }
    factory<MusicFetcher> {
        MusicFetcherAndroidImpl(
            context = androidContext(),
            playlistRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
            folderRepository = get()
        )
    }
    factory<SoulSearchingSettings> {
        SoulSearchingSettingsImpl(
            settings = SharedPreferencesSettings(
                delegate = androidApplication().getSharedPreferences(
                    SoulSearchingSettings.SHARED_PREF_KEY,
                    Context.MODE_PRIVATE
                )
            )
        )
    }
    single<PlaybackManagerAndroidImpl> {
        PlaybackManagerAndroidImpl(
            context = androidContext()
        )
    }
    single<MusicFetcherAndroidImpl> {
        MusicFetcherAndroidImpl(
            context = androidContext(),
            playlistRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            albumArtistRepository = get(),
            imageCoverRepository = get(),
            folderRepository = get()
        )
    }
    single<ColorThemeManager> {
        ColorThemeManager(
            settings = get()
        )
    }
    single<ViewSettingsManager> {
        ViewSettingsManager(
            settings = get()
        )
    }
}
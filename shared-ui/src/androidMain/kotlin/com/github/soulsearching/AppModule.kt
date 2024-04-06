package com.github.soulsearching

import android.content.Context
import com.github.soulsearching.model.utils.MusicFetcherAndroidImpl
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.model.settings.SoulSearchingSettingsImpl
import com.github.soulsearching.model.settings.ViewSettingsManager
import com.github.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllAlbumsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllQuickAccessViewModel
import com.github.soulsearching.viewmodel.AllQuickAccessViewModelAndroidImpl
import com.github.soulsearching.viewmodel.MainActivityViewModel
import com.github.soulsearching.viewmodel.MainActivityViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.viewmodel.ModifyAlbumViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyArtistViewModel
import com.github.soulsearching.viewmodel.ModifyArtistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.viewmodel.ModifyMusicViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.viewmodel.PlayerMusicListViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerViewModel
import com.github.soulsearching.viewmodel.PlayerViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedAlbumViewModel
import com.github.soulsearching.viewmodel.SelectedAlbumViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.viewmodel.SelectedArtistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModel
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModel
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModelAndroidImpl
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val appModule = module {
    single<SettingsAddMusicsViewModel> {
        SettingsAddMusicsViewModelAndroidImpl(
            folderRepository = get(),
            musicRepository = get(),
            musicFetcher = get()
        )
    }
    single<AllAlbumsViewModel> {
        AllAlbumsViewModelAndroidImpl(
            albumRepository = get(),
            musicRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            settings = get()
        )
    }
    single<AllArtistsViewModel> {
        AllArtistsViewModelAndroidImpl(
            artistRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            settings = get()
        )
    }
    single<SettingsAllFoldersViewModel> {
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
    single<AllImageCoversViewModel> {
        AllImageCoversViewModelAndroidImpl(
            imageCoverRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            playlistRepository = get()
        )
    }
    single<AllMusicsViewModel> {
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
    single<AllPlaylistsViewModel> {
        AllPlaylistsViewModelAndroidImpl(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            imageCoverRepository = get(),
            settings = get()
        )
    }
    single<AllQuickAccessViewModel> {
        AllQuickAccessViewModelAndroidImpl(
            musicRepository = get(),
            playlistRepository = get(),
            albumRepository = get(),
            artistRepository = get()
        )
    }
    single<MainActivityViewModel> {
        MainActivityViewModelAndroidImpl(
            settings = get()
        )
    }
    single<ModifyAlbumViewModel> {
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
    single<ModifyArtistViewModel> {
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
    single<ModifyMusicViewModel> {
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
    single<ModifyPlaylistViewModel> {
        ModifyPlaylistViewModelAndroidImpl(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            imageCoverRepository = get(),
            settings = get()
        )
    }
    single<PlayerMusicListViewModel> {
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
    single<PlayerViewModel> {
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
    single<SelectedAlbumViewModel> {
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
    single<SelectedArtistViewModel> {
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
    single<SelectedPlaylistViewModel> {
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
    single<PlaybackManager> {
        PlaybackManagerAndroidImpl(
            context = androidContext(),
            settings = get()
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
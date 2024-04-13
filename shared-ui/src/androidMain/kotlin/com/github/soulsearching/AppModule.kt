package com.github.soulsearching

import android.content.Context
import com.github.soulsearching.model.utils.MusicFetcherAndroidImpl
import com.github.soulsearching.domain.model.MusicFetcher
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.domain.model.settings.SoulSearchingSettingsImpl
import com.github.soulsearching.settings.mainpagepersonalisation.domain.ViewSettingsManager
import com.github.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.domain.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllAlbumsViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.AllQuickAccessViewModel
import com.github.soulsearching.viewmodel.AllQuickAccessViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.MainActivityViewModel
import com.github.soulsearching.viewmodel.MainActivityViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.viewmodel.ModifyAlbumViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.ModifyArtistViewModel
import com.github.soulsearching.viewmodel.ModifyArtistViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.viewmodel.ModifyMusicViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.viewmodel.PlayerViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.SelectedAlbumViewModel
import com.github.soulsearching.viewmodel.SelectedAlbumViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.viewmodel.SelectedArtistViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.SettingsAddMusicsViewModel
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModelAndroidImpl
import com.github.soulsearching.domain.viewmodel.SettingsAllFoldersViewModel
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
            musicRepository = get()
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
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            settings = get(),
            context = androidContext(),
            playbackManager = get(),
            musicFetcher = get(),
            playlistRepository = get()
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
            albumRepository = get(),
            imageCoverRepository = get(),
            playbackManager = get()
        )
    }
    single<ModifyArtistViewModel> {
        ModifyArtistViewModelAndroidImpl(
            artistRepository = get(),
            imageCoverRepository = get()
        )
    }
    single<ModifyMusicViewModel> {
        ModifyMusicViewModelAndroidImpl(
            musicRepository = get(),
            imageCoverRepository = get(),
            playbackManager = get()
        )
    }
    single<ModifyPlaylistViewModel> {
        ModifyPlaylistViewModelAndroidImpl(
            playlistRepository = get(),
            imageCoverRepository = get(),
        )
    }
    single<PlayerViewModel> {
        PlayerViewModelAndroidImpl(
            musicRepository = get(),
            playbackManager = get(),
            colorThemeManager = get(),
            musicPlaylistRepository = get(),
            playlistRepository = get()
        )
    }
    single<SelectedAlbumViewModel> {
        SelectedAlbumViewModelAndroidImpl(
            albumRepository = get(),
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            playbackManager = get()
        )
    }
    single<SelectedArtistViewModel> {
        SelectedArtistViewModelAndroidImpl(
            artistRepository = get(),
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            playbackManager = get()
        )
    }
    single<SelectedPlaylistViewModel> {
        SelectedPlaylistViewModelAndroidImpl(
            playlistRepository = get(),
            musicRepository = get(),
            musicPlaylistRepository = get(),
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
            settings = get(),
            playerMusicRepository = get(),
            musicRepository = get()
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
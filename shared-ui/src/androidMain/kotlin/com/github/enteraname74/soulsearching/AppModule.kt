package com.github.enteraname74.soulsearching

import android.content.Context
import com.github.enteraname74.soulsearching.model.utils.MusicFetcherAndroidImpl
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsImpl
import com.github.enteraname74.soulsearching.feature.settings.domain.ViewSettingsManager
import com.github.enteraname74.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.viewmodel.AllAlbumsViewModel
import com.github.enteraname74.soulsearching.viewmodel.AllAlbumsViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.AllArtistsViewModel
import com.github.enteraname74.soulsearching.viewmodel.AllArtistsViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.viewmodel.AllMusicsViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.enteraname74.soulsearching.viewmodel.AllPlaylistsViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.AllQuickAccessViewModel
import com.github.enteraname74.soulsearching.viewmodel.AllQuickAccessViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.MainActivityViewModel
import com.github.enteraname74.soulsearching.viewmodel.MainActivityViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.viewmodel.ModifyAlbumViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.viewmodel.ModifyArtistViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.viewmodel.ModifyMusicViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.viewmodel.ModifyPlaylistViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.enteraname74.soulsearching.viewmodel.PlayerViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.viewmodel.SelectedAlbumViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedArtistViewModel
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedFolderViewModel
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedMonthViewModel
import com.github.enteraname74.soulsearching.viewmodel.SelectedArtistViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.SelectedPlaylistViewModel
import com.github.enteraname74.soulsearching.viewmodel.SelectedPlaylistViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.viewmodel.SettingsAddMusicsViewModelAndroidImpl
import com.github.enteraname74.soulsearching.domain.viewmodel.SettingsAllFoldersViewModel
import com.github.enteraname74.soulsearching.viewmodel.SelectedFolderViewModelAndroidImpl
import com.github.enteraname74.soulsearching.viewmodel.SelectedMonthViewModelAndroidImpl
import com.github.enteraname74.soulsearching.viewmodel.SettingsAllFoldersViewModelAndroidImpl
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
            settings = get(),
            musicRepository = get(),
            playbackManager = get()
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
            playbackManager = get(),
            artistRepository = get()
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
            playbackManager = get(),
            albumRepository = get(),
            artistRepository = get()
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
            playlistRepository = get(),
            lyricsProvider = get()
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
            playbackManager = get(),
            albumRepository = get()
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
    single<SelectedFolderViewModel> {
        SelectedFolderViewModelAndroidImpl(
            playlistRepository = get(),
            playbackManager = get(),
            musicRepository = get(),
            musicPlaylistRepository = get()
        )
    }
    single<SelectedMonthViewModel> {
        SelectedMonthViewModelAndroidImpl(
            playlistRepository = get(),
            playbackManager = get(),
            musicRepository = get(),
            musicPlaylistRepository = get()
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
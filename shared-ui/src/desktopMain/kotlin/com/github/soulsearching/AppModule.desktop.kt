package com.github.soulsearching

import com.github.soulsearching.classes.MusicFetcherDesktopImpl
import com.github.soulsearching.classes.PlaybackManagerDesktopImpl
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.model.settings.SoulSearchingSettingsImpl
import com.github.soulsearching.model.settings.ViewSettingsManager
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.viewmodel.AllAlbumsViewModelDesktopImpl
import com.github.soulsearching.viewmodel.AllArtistsViewModelDesktopImpl
import com.github.soulsearching.viewmodel.AllImageCoversViewModelDesktopImpl
import com.github.soulsearching.viewmodel.AllMusicsViewModelDesktopImpl
import com.github.soulsearching.viewmodel.AllPlaylistsViewModelDesktopImpl
import com.github.soulsearching.viewmodel.AllQuickAccessViewModelDesktopImpl
import com.github.soulsearching.viewmodel.MainActivityViewModelDesktopImpl
import com.github.soulsearching.viewmodel.ModifyAlbumViewModelDesktopImpl
import com.github.soulsearching.viewmodel.ModifyArtistViewModelDesktopImpl
import com.github.soulsearching.viewmodel.ModifyMusicViewModelDesktopImpl
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModelDesktopImpl
import com.github.soulsearching.viewmodel.NavigationViewModelDesktopImpl
import com.github.soulsearching.viewmodel.PlayerMusicListViewModelDesktopImpl
import com.github.soulsearching.viewmodel.PlayerViewModelDesktopImpl
import com.github.soulsearching.viewmodel.SelectedAlbumViewModelDesktopImpl
import com.github.soulsearching.viewmodel.SelectedArtistViewModelDesktopImpl
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModelDesktopImpl
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModelDesktopImpl
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModelDesktopImpl
import com.russhwolf.settings.PreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val appModule: Module = module {
    single {
        SettingsAddMusicsViewModelDesktopImpl(
            folderRepository = get(),
            musicRepository = get(),
            musicFetcher = get()
        )
    }
    single {
        AllAlbumsViewModelDesktopImpl(
            albumRepository = get(),
            musicRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            settings = get()
        )
    }
    single {
        AllArtistsViewModelDesktopImpl(
            artistRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            settings = get()
        )
    }
    single {
        SettingsAllFoldersViewModelDesktopImpl(
            folderRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            albumArtistRepository = get(),
            musicAlbumRepository = get(),
            musicArtistRepository = get()
        )
    }
    single {
        AllImageCoversViewModelDesktopImpl(
            imageCoverRepository = get(),
            musicRepository = get(),
            albumRepository = get(),
            artistRepository = get(),
            playlistRepository = get()
        )
    }
    single {
        AllMusicsViewModelDesktopImpl(
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
            playbackManager = get(),
            musicFetcher = get()
        )
    }
    single {
        AllPlaylistsViewModelDesktopImpl(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            imageCoverRepository = get(),
            settings = get()
        )
    }
    single {
        AllQuickAccessViewModelDesktopImpl(
            musicRepository = get(),
            playlistRepository = get(),
            albumRepository = get(),
            artistRepository = get()
        )
    }
    single {
        MainActivityViewModelDesktopImpl(
            settings = get()
        )
    }
    single {
        ModifyAlbumViewModelDesktopImpl(
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
    single {
        ModifyArtistViewModelDesktopImpl(
            musicRepository = get(),
            artistRepository = get(),
            musicArtistRepository = get(),
            musicAlbumRepository = get(),
            albumArtistRepository = get(),
            albumRepository = get(),
            imageCoverRepository = get()
        )
    }
    single {
        ModifyMusicViewModelDesktopImpl(
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
    single {
        ModifyPlaylistViewModelDesktopImpl(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            imageCoverRepository = get(),
            settings = get()
        )
    }
    single {
        PlayerMusicListViewModelDesktopImpl(
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
    single {
        PlayerViewModelDesktopImpl(
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
    single {
        SelectedAlbumViewModelDesktopImpl(
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
    single {
        SelectedArtistViewModelDesktopImpl(
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
    single {
        SelectedArtistViewModelDesktopImpl(
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
    single {
        SelectedPlaylistViewModelDesktopImpl(
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
    single {
        NavigationViewModelDesktopImpl()
    }
    factory<MusicFetcher> {
        MusicFetcherDesktopImpl(
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
            settings = PreferencesSettings(
                delegate = Preferences.userRoot()
            )
        )
    }
    single<PlaybackManagerDesktopImpl> {
        PlaybackManagerDesktopImpl()
    }
    single<MusicFetcherDesktopImpl> {
        MusicFetcherDesktopImpl(
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
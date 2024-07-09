package com.github.enteraname74.soulsearching

import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsImpl
import com.github.enteraname74.soulsearching.model.MusicFetcherDesktopImpl
import com.github.enteraname74.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.enteraname74.soulsearching.feature.settings.domain.ViewSettingsManager
import com.github.enteraname74.soulsearching.viewmodel.AllAlbumsViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.AllArtistsViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.AllMusicsViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.AllPlaylistsViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.AllQuickAccessViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.MainActivityViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.ModifyAlbumViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.ModifyArtistViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.ModifyMusicViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.ModifyPlaylistViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.NavigationViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.PlayerViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.SelectedAlbumViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.SelectedArtistViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.SelectedFolderViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.SelectedMonthViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.SelectedPlaylistViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.SettingsAddMusicsViewModelDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.SettingsAllFoldersViewModelDesktopImpl
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
            musicRepository = get()
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
            musicAlbumRepository = get(),
            musicArtistRepository = get(),
            settings = get(),
            playbackManager = get(),
            musicFetcher = get()
        )
    }
    single {
        AllPlaylistsViewModelDesktopImpl(
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            settings = get(),
            musicRepository = get(),
            playbackManager = get()
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
            albumRepository = get(),
            artistRepository = get(),
            imageCoverRepository = get(),
            playbackManager = get()
        )
    }
    single {
        ModifyArtistViewModelDesktopImpl(
            artistRepository = get(),
            imageCoverRepository = get()
        )
    }
    single {
        ModifyMusicViewModelDesktopImpl(
            musicRepository = get(),
            artistRepository = get(),
            albumRepository = get(),
            imageCoverRepository = get(),
            playbackManager = get()
        )
    }
    single {
        ModifyPlaylistViewModelDesktopImpl(
            playlistRepository = get(),
            imageCoverRepository = get()
        )
    }
    single {
        PlayerViewModelDesktopImpl(
            musicRepository = get(),
            playbackManager = get(),
            colorThemeManager = get(),
            musicPlaylistRepository = get(),
            playlistRepository = get(),
            lyricsProvider = get()
        )
    }
    single {
        SelectedAlbumViewModelDesktopImpl(
            albumRepository = get(),
            musicRepository = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            playbackManager = get()
        )
    }
    single {
        SelectedArtistViewModelDesktopImpl(
            playlistRepository = get(),
            musicRepository = get(),
            artistRepository = get(),
            musicPlaylistRepository = get(),
            playbackManager = get(),
            albumRepository = get()
        )
    }
    single {
        SelectedPlaylistViewModelDesktopImpl(
            playlistRepository = get(),
            musicRepository = get(),
            musicPlaylistRepository = get(),
            playbackManager = get()
        )
    }
    single {
        SelectedFolderViewModelDesktopImpl(
            playbackManager = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            musicRepository = get()
        )
    }
    single {
        SelectedMonthViewModelDesktopImpl(
            playbackManager = get(),
            playlistRepository = get(),
            musicPlaylistRepository = get(),
            musicRepository = get()
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
        PlaybackManagerDesktopImpl(
            settings = get(),
            playerMusicRepository = get(),
            musicRepository = get()
        )
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
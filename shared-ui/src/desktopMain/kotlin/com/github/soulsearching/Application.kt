package com.github.soulsearching

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.domainModule
import com.github.enteraname74.localdb.localDbModule
import com.github.soulsearching.classes.PlaybackManagerDesktopImpl
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.viewmodel.*
import org.koin.compose.KoinApplication

@Composable
fun Application() {
    KoinApplication(
        application = {
            modules(appModule, domainModule, localDbModule)
        }
    ) {
        val colorThemeManager: ColorThemeManager = injectElement()
        SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()

        val allMusicsViewModel = injectElement<AllMusicsViewModelDesktopImpl>()
        val allPlaylistsViewModel = injectElement<AllPlaylistsViewModelDesktopImpl>()
        val allAlbumsViewModel = injectElement<AllAlbumsViewModelDesktopImpl>()
        val allArtistsViewModel = injectElement<AllArtistsViewModelDesktopImpl>()
        val allImageCoversViewModel = injectElement<AllImageCoversViewModelDesktopImpl>()
        val allQuickAccessViewModel = injectElement<AllQuickAccessViewModelDesktopImpl>()

        // Selected page view models
        val selectedPlaylistViewModel = injectElement<SelectedPlaylistViewModelDesktopImpl>()
        val selectedAlbumViewModel = injectElement<SelectedAlbumViewModelDesktopImpl>()
        val selectedArtistsViewModel = injectElement<SelectedArtistViewModelDesktopImpl>()

        // Modify page view models
        val modifyPlaylistViewModel = injectElement<ModifyPlaylistViewModelDesktopImpl>()
        val modifyAlbumViewModel = injectElement<ModifyAlbumViewModelDesktopImpl>()
        val modifyArtistViewModel = injectElement<ModifyArtistViewModelDesktopImpl>()
        val modifyMusicViewModel = injectElement<ModifyMusicViewModelDesktopImpl>()

        // Player view model :
        val playerViewModel = injectElement<PlayerViewModelDesktopImpl>()
        val playerMusicListViewModel = injectElement<PlayerMusicListViewModelDesktopImpl>()

        // Settings view models:
        val settingsAllFoldersViewModel =
            injectElement<SettingsAllFoldersViewModelDesktopImpl>()
        val settingsAddMusicsViewModel = injectElement<SettingsAddMusicsViewModelDesktopImpl>()

        val mainActivityViewModel = injectElement<MainActivityViewModelDesktopImpl>()
        val navigationViewModel = injectElement<NavigationViewModelDesktopImpl>()
        val playbackManager = injectElement<PlaybackManagerDesktopImpl>()

        with(mainActivityViewModel.handler) {
            isReadPermissionGranted = true
            isPostNotificationGranted = true
            hasMusicsBeenFetched = true
        }

        SoulSearchingApplication(
            allMusicsViewModel = allMusicsViewModel,
            allPlaylistsViewModel = allPlaylistsViewModel,
            allAlbumsViewModel = allAlbumsViewModel,
            allArtistsViewModel = allArtistsViewModel,
            allImageCoversViewModel = allImageCoversViewModel,
            playerMusicListViewModel = playerMusicListViewModel,
            allQuickAccessViewModel = allQuickAccessViewModel,
            settingsAllFoldersViewModel = settingsAllFoldersViewModel,
            mainActivityViewModel = mainActivityViewModel,
            selectedAlbumViewModel = selectedAlbumViewModel,
            selectedArtistViewModel = selectedArtistsViewModel,
            selectedPlaylistViewModel = selectedPlaylistViewModel,
            modifyAlbumViewModel = modifyAlbumViewModel,
            modifyArtistViewModel = modifyArtistViewModel,
            modifyMusicViewModel = modifyMusicViewModel,
            modifyPlaylistViewModel = modifyPlaylistViewModel,
            settingsAddMusicsViewModel = settingsAddMusicsViewModel,
            navigationViewModel = navigationViewModel,
            playerViewModel = playerViewModel,
            playbackManager = playbackManager
        )
    }

}
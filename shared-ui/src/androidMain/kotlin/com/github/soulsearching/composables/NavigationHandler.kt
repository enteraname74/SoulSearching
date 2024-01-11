package com.github.soulsearching.composables

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.soulsearching.Constants
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.draggablestates.PlayerDraggableState
import com.github.soulsearching.draggablestates.SearchDraggableState
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.screens.MainPageScreen
import com.github.soulsearching.screens.ModifyAlbumScreen
import com.github.soulsearching.screens.ModifyArtistScreen
import com.github.soulsearching.screens.ModifyMusicScreen
import com.github.soulsearching.screens.ModifyPlaylistScreen
import com.github.soulsearching.screens.MoreAlbumsScreen
import com.github.soulsearching.screens.MoreArtistsScreen
import com.github.soulsearching.screens.MorePlaylistsScreen
import com.github.soulsearching.screens.SelectedAlbumScreen
import com.github.soulsearching.screens.SelectedArtistScreen
import com.github.soulsearching.screens.SelectedPlaylistScreen
import com.github.soulsearching.screens.settings.SettingsAboutScreen
import com.github.soulsearching.screens.settings.SettingsAddMusicsScreen
import com.github.soulsearching.screens.settings.SettingsColorThemeScreen
import com.github.soulsearching.screens.settings.SettingsDevelopersScreen
import com.github.soulsearching.screens.settings.SettingsManageMusicsScreen
import com.github.soulsearching.screens.settings.SettingsPersonalisationScreen
import com.github.soulsearching.screens.settings.SettingsScreen
import com.github.soulsearching.screens.settings.SettingsUsedFoldersScreen
import com.github.soulsearching.states.AlbumState
import com.github.soulsearching.states.ArtistState
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.states.QuickAccessState
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.viewmodel.ModifyArtistViewModel
import com.github.soulsearching.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.viewmodel.SelectedAlbumViewModel
import com.github.soulsearching.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModel
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModel

@Composable
fun NavigationHandler(
    navController: NavHostController,
    colorThemeManager: ColorThemeManager = injectElement(),
    playerDraggableState: PlayerDraggableState,
    searchDraggableState: SearchDraggableState,
    musicState: MusicState,
    playlistState: PlaylistState,
    albumState: AlbumState,
    artistState: ArtistState,
    quickAccessState: QuickAccessState,
    allPlaylistsViewModel: AllPlaylistsViewModel,
    allImageCoversViewModel: AllImageCoversViewModel,
    modifyPlaylistViewModel: ModifyPlaylistViewModel,
    allFoldersViewModel: SettingsAllFoldersViewModel,
    addMusicsViewModel: SettingsAddMusicsViewModel,
    allMusicsViewModel: AllMusicsViewModel,
    allAlbumsViewModel: AllAlbumsViewModel,
    allArtistsViewModel: AllArtistsViewModel,
    selectedArtistViewModel: SelectedArtistViewModel,
    selectedAlbumViewModel: SelectedAlbumViewModel,
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    modifyMusicViewModel: ModifyMusicViewModel,
    modifyAlbumViewModel: ModifyAlbumViewModel,
    modifyArtistViewModel: ModifyArtistViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
    settingsAllFoldersViewModel: SettingsAllFoldersViewModel
) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = "mainPage",
        enterTransition =
        { fadeIn(animationSpec = tween(Constants.AnimationDuration.normal)) },
        exitTransition =
        { fadeOut(animationSpec = tween(Constants.AnimationDuration.normal)) },
    ) {
        composable("mainPage") {
            MainPageScreen(
                navigateToPlaylist = {
                    navController.navigate("selectedPlaylist/$it")
                },
                navigateToAlbum = {
                    navController.navigate("selectedAlbum/$it")
                },
                navigateToArtist = {
                    navController.navigate("selectedArtist/$it")
                },
                navigateToMorePlaylist = {
                    navController.navigate("morePlaylists")
                },
                navigateToMoreArtists = {
                    navController.navigate("moreArtists")
                },
                navigateToMoreShortcuts = {
                    navController.navigate("moreShortcuts")
                },
                navigateToMoreAlbums = {
                    navController.navigate("moreAlbums")
                },
                navigateToModifyMusic = {
                    navController.navigate("modifyMusic/$it")
                },
                navigateToModifyPlaylist = {
                    navController.navigate("modifyPlaylist/$it")
                },
                navigateToModifyAlbum = {
                    navController.navigate("modifyAlbum/$it")
                },
                navigateToModifyArtist = {
                    navController.navigate("modifyArtist/$it")
                },
                navigateToSettings = {
                    navController.navigate("settings")
                },
                playerDraggableState = playerDraggableState,
                searchDraggableState = searchDraggableState,
                musicState = musicState,
                playlistState = playlistState,
                albumState = albumState,
                artistState = artistState,
                quickAccessState = quickAccessState,
                allAlbumsViewModel = allAlbumsViewModel,
                allMusicsViewModel = allMusicsViewModel,
                allPlaylistsViewModel = allPlaylistsViewModel,
                allArtistsViewModel = allArtistsViewModel,
                allImageCoversViewModel = allImageCoversViewModel,
                playerMusicListViewModel = playerMusicListViewModel
            )
        }
        composable(
            "selectedPlaylist/{playlistId}",
            arguments = listOf(navArgument("playlistId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            SelectedPlaylistScreen(
                navigateToModifyPlaylist = {
                    navController.navigate(
                        "modifyPlaylist/" + backStackEntry.arguments?.getString(
                            "playlistId"
                        )
                    )
                },
                selectedPlaylistId = backStackEntry.arguments?.getString("playlistId")!!,
                playlistState = playlistState,
                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                navigateBack = {
                    colorThemeManager.removePlaylistTheme()
                    navController.popBackStack()
                },
                retrieveCoverMethod = {
                    allImageCoversViewModel.handler.getImageCover(
                        it
                    )
                },
                playerDraggableState = playerDraggableState,
                selectedPlaylistViewModel = selectedPlaylistViewModel,
                playerMusicListViewModel = playerMusicListViewModel
            )
        }
        composable(
            "selectedAlbum/{albumId}",
            arguments = listOf(navArgument("albumId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            SelectedAlbumScreen(
                navigateToModifyAlbum = {
                    navController.navigate(
                        "modifyAlbum/" + backStackEntry.arguments?.getString(
                            "albumId"
                        )
                    )
                },
                selectedAlbumId = backStackEntry.arguments?.getString("albumId")!!,
                playlistState = playlistState,
                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                navigateBack = {
                    colorThemeManager.removePlaylistTheme()
                    navController.popBackStack()
                },
                retrieveCoverMethod = {
                    allImageCoversViewModel.handler.getImageCover(
                        it
                    )
                },
                playerDraggableState = playerDraggableState,
                playerMusicListViewModel = playerMusicListViewModel,
                selectedAlbumViewModel = selectedAlbumViewModel
            )
        }
        composable(
            "selectedArtist/{artistId}",
            arguments = listOf(navArgument("artistId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            SelectedArtistScreen(
                navigateToModifyArtist = {
                    navController.navigate(
                        "modifyArtist/" + backStackEntry.arguments?.getString(
                            "artistId"
                        )
                    )
                },
                selectedArtistId = backStackEntry.arguments?.getString("artistId")!!,
                playlistState = playlistState,
                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                navigateBack = {
                    colorThemeManager.removePlaylistTheme()
                    navController.popBackStack()
                },
                retrieveCoverMethod = {
                    allImageCoversViewModel.handler.getImageCover(
                        it
                    )
                },
                playerDraggableState = playerDraggableState,
                playerMusicListViewModel = playerMusicListViewModel,
                selectedArtistViewModel = selectedArtistViewModel
            )
        }
        composable(
            "modifyPlaylist/{playlistId}",
            arguments = listOf(navArgument("playlistId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            ModifyPlaylistScreen(
                modifyPlaylistViewModel = modifyPlaylistViewModel,
                selectedPlaylistId = backStackEntry.arguments?.getString("playlistId")!!,
                finishAction = { navController.popBackStack() }
            )
        }
        composable(
            "modifyMusic/{musicId}",
            arguments = listOf(navArgument("musicId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            ModifyMusicScreen(
                selectedMusicId = backStackEntry.arguments?.getString("musicId")!!,
                finishAction = { navController.popBackStack() },
                modifyMusicViewModel = modifyMusicViewModel
            )
        }
        composable(
            "modifyAlbum/{albumId}",
            arguments = listOf(navArgument("albumId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            ModifyAlbumScreen(
                selectedAlbumId = backStackEntry.arguments?.getString("albumId")!!,
                finishAction = { navController.popBackStack() },
                modifyAlbumViewModel = modifyAlbumViewModel
            )
        }
        composable(
            "modifyArtist/{artistId}",
            arguments = listOf(navArgument("artistId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            ModifyArtistScreen(
                selectedArtistId = backStackEntry.arguments?.getString("artistId")!!,
                finishAction = { navController.popBackStack() },
                modifyArtistViewModel = modifyArtistViewModel
            )
        }
        composable(
            "morePlaylists"
        ) {
            MorePlaylistsScreen(
                navigateToSelectedPlaylist = { navController.navigate("selectedPlaylist/$it") },
                finishAction = { navController.popBackStack() },
                navigateToModifyPlaylist = { navController.navigate("modifyPlaylist/$it") },
                retrieveCoverMethod = {
                    allImageCoversViewModel.handler.getImageCover(
                        it
                    )
                },
                allPlaylistsViewModel = allPlaylistsViewModel
            )
        }
        composable(
            "moreAlbums"
        ) {
            MoreAlbumsScreen(
                navigateToSelectedAlbum = { navController.navigate("selectedAlbum/$it") },
                finishAction = { navController.popBackStack() },
                navigateToModifyAlbum = { navController.navigate("modifyAlbum/$it") },
                retrieveCoverMethod = {
                    allImageCoversViewModel.handler.getImageCover(
                        it
                    )
                },
                allAlbumsViewModel = allAlbumsViewModel
            )
        }
        composable(
            "moreArtists"
        ) {
            MoreArtistsScreen(
                navigateToSelectedArtist = { navController.navigate("selectedArtist/$it") },
                finishAction = { navController.popBackStack() },
                navigateToModifyArtist = { navController.navigate("modifyArtist/$it") },
                retrieveCoverMethod = {
                    allImageCoversViewModel.handler.getImageCover(
                        it
                    )
                },
                allArtistsViewModel = allArtistsViewModel
            )
        }
        composable(
            "settings"
        ) {
            SettingsScreen(
                finishAction = { navController.popBackStack() },
                navigateToColorTheme = {
                    navController.navigate("colorTheme")
                },
                navigateToManageMusics = {
                    navController.navigate("manageMusics")
                },
                navigateToPersonalisation = {
                    navController.navigate("personalisation")
                },
                navigateToAbout = {
                    navController.navigate("about")
                }
            )
        }
        composable(
            "personalisation"
        ) {
            SettingsPersonalisationScreen(
                finishAction = { navController.popBackStack() }
            )
        }
        composable(
            "manageMusics"
        ) {
            SettingsManageMusicsScreen(
                finishAction = { navController.popBackStack() },
                navigateToFolders = {
                    allFoldersViewModel.handler.onFolderEvent(
                        FolderEvent.FetchFolders
                    )
                    navController.navigate("usedFolders")
                },
                navigateToAddMusics = {
                    addMusicsViewModel.handler.onAddMusicEvent(AddMusicsEvent.ResetState)
                    navController.navigate("addMusics")
                }
            )
        }
        composable(
            "usedFolders"
        ) {
            SettingsUsedFoldersScreen(
                finishAction = { navController.popBackStack() },
                settingsAllFoldersViewModel = settingsAllFoldersViewModel
            )
        }
        composable(
            "addMusics"
        ) {
            SettingsAddMusicsScreen(
                finishAction = { navController.popBackStack() },
                saveMusicFunction = allMusicsViewModel.handler::addMusic,
                addMusicsViewModel = addMusicsViewModel
            )
        }
        composable(
            "colorTheme"
        ) {
            SettingsColorThemeScreen(
                finishAction = { navController.popBackStack() },
                updateColorThemeMethod = colorThemeManager::updateColorTheme
            )
        }
        composable(
            "about"
        ) {
            SettingsAboutScreen(
                finishAction = { navController.popBackStack() },
                navigateToDevelopers = { navController.navigate("developers") }
            )
        }
        composable(
            "developers"
        ) {
            SettingsDevelopersScreen(
                finishAction = { navController.popBackStack() }
            )
        }
    }
}
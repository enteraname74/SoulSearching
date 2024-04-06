package com.github.soulsearching.composables

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.navigation.NavigationController
import com.github.soulsearching.navigation.NavigationHost
import com.github.soulsearching.navigation.Route
import com.github.soulsearching.navigation.RoutesNames
import com.github.soulsearching.navigation.Screen
import com.github.soulsearching.screens.MainPageScreenView
import com.github.soulsearching.screens.ModifyAlbumScreenView
import com.github.soulsearching.screens.ModifyArtistScreenView
import com.github.soulsearching.screens.ModifyMusicScreenView
import com.github.soulsearching.screens.ModifyPlaylistScreenView
import com.github.soulsearching.screens.SelectedAlbumScreenView
import com.github.soulsearching.screens.SelectedArtistScreenView
import com.github.soulsearching.screens.SelectedPlaylistScreenView
import com.github.soulsearching.screens.settings.SettingsAboutScreenView
import com.github.soulsearching.screens.settings.SettingsAddMusicsScreenView
import com.github.soulsearching.screens.settings.SettingsColorThemeScreenView
import com.github.soulsearching.screens.settings.SettingsDevelopersScreenView
import com.github.soulsearching.screens.settings.SettingsManageMusicsScreenView
import com.github.soulsearching.screens.settings.SettingsPersonalisationScreenView
import com.github.soulsearching.screens.settings.SettingsScreenView
import com.github.soulsearching.screens.settings.SettingsUsedFoldersScreenView
import com.github.soulsearching.states.AlbumState
import com.github.soulsearching.states.ArtistState
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.states.QuickAccessState
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.AllQuickAccessViewModel
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavigationHandler(
    colorThemeManager: ColorThemeManager = injectElement(),
    playerDraggableState: SwipeableState<BottomSheetStates>,
    playerMusicListDraggableState: SwipeableState<BottomSheetStates>,
    searchDraggableState: SwipeableState<BottomSheetStates>,
    allQuickAccessViewModel: AllQuickAccessViewModel = injectElement(),
    allPlaylistsViewModel: AllPlaylistsViewModel = injectElement(),
    allImageCoversViewModel: AllImageCoversViewModel = injectElement(),
    modifyPlaylistViewModel: ModifyPlaylistViewModel = injectElement(),
    allFoldersViewModel: SettingsAllFoldersViewModel = injectElement(),
    addMusicsViewModel: SettingsAddMusicsViewModel = injectElement(),
    allMusicsViewModel: AllMusicsViewModel = injectElement(),
    allAlbumsViewModel: AllAlbumsViewModel = injectElement(),
    allArtistsViewModel: AllArtistsViewModel = injectElement(),
    selectedArtistViewModel: SelectedArtistViewModel = injectElement(),
    selectedAlbumViewModel: SelectedAlbumViewModel = injectElement(),
    selectedPlaylistViewModel: SelectedPlaylistViewModel = injectElement(),
    modifyMusicViewModel: ModifyMusicViewModel = injectElement(),
    modifyAlbumViewModel: ModifyAlbumViewModel = injectElement(),
    modifyArtistViewModel: ModifyArtistViewModel = injectElement(),
    playerMusicListViewModel: PlayerMusicListViewModel = injectElement(),
    settingsAllFoldersViewModel: SettingsAllFoldersViewModel = injectElement(),
    navigationController: NavigationController<RoutesNames>
) {

    val musicState by allMusicsViewModel.handler.state.collectAsState()
    val playlistState by allPlaylistsViewModel.handler.state.collectAsState()
    val albumState by allAlbumsViewModel.handler.state.collectAsState()
    val artistState by allArtistsViewModel.handler.state.collectAsState()
    val quickAccessState by allQuickAccessViewModel.handler.state.collectAsState()


    NavigationHost(
        playerDraggableState = playerDraggableState,
        playerMusicListDraggableState =  playerMusicListDraggableState,
        navigationController = navigationController,
        screens = listOf(
            Screen(
                screenRoute = RoutesNames.MAIN_PAGE_SCREEN,
                screen = {
                    MainPageScreenView(
                        allMusicsViewModel = allMusicsViewModel,
                        allPlaylistsViewModel = allPlaylistsViewModel,
                        allAlbumsViewModel = allAlbumsViewModel,
                        allArtistsViewModel = allArtistsViewModel,
                        allImageCoversViewModel = allImageCoversViewModel,
                        playerMusicListViewModel = playerMusicListViewModel,
                        navigateToPlaylist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.SELECTED_PLAYLIST_SCREEN,
                                    arguments = mapOf(Pair("playlistId", it))
                                )
                            )
                        },
                        navigateToAlbum = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.SELECTED_ALBUM_SCREEN,
                                    arguments = mapOf(Pair("albumId", it))
                                )
                            )
                        },
                        navigateToArtist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.SELECTED_ARTIST_SCREEN,
                                    arguments = mapOf(Pair("artistId", it))
                                )
                            )
                        },
                        navigateToModifyMusic = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_MUSIC_SCREEN,
                                    arguments = mapOf(Pair("musicId", it))
                                )
                            )
                        },
                        navigateToModifyPlaylist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_PLAYLIST_SCREEN,
                                    arguments = mapOf(Pair("playlistId", it))
                                )
                            )
                        },
                        navigateToModifyAlbum = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_ALBUM_SCREEN,
                                    arguments = mapOf(Pair("albumId", it))
                                )
                            )
                        },
                        navigateToModifyArtist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_ARTIST_SCREEN,
                                    arguments = mapOf(Pair("artistId", it))
                                )
                            )
                        },
                        navigateToSettings = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.SETTINGS_SCREEN
                                )
                            )
                        },
                        playerDraggableState = playerDraggableState,
                        searchDraggableState = searchDraggableState,
                        musicState = musicState,
                        playlistState = playlistState,
                        albumState = albumState,
                        artistState = artistState,
                        quickAccessState = quickAccessState
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SELECTED_PLAYLIST_SCREEN,
                screen = {
                    SelectedPlaylistScreenView(
                        navigateToModifyPlaylist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_PLAYLIST_SCREEN,
                                    arguments = mapOf(Pair("playlistId", it))
                                )
                            )
                        },
                        selectedPlaylistId = navigationController.getArgument("playlistId") as String?
                            ?: "",
                        playlistState = playlistState,
                        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                        navigateToModifyMusic = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_MUSIC_SCREEN,
                                    arguments = mapOf(Pair("musicId", it))
                                )
                            )
                        },
                        navigateBack = {
                            colorThemeManager.removePlaylistTheme()
                            navigationController.navigateBack()
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
            ),
            Screen(
                screenRoute = RoutesNames.SELECTED_ALBUM_SCREEN,
                screen = {
                    SelectedAlbumScreenView(
                        navigateToModifyAlbum = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_ALBUM_SCREEN,
                                    arguments = mapOf(Pair("albumId", it))
                                )
                            )
                        },
                        selectedAlbumId = navigationController.getArgument("albumId") as String?
                            ?: "",
                        playlistState = playlistState,
                        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                        navigateToModifyMusic = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_MUSIC_SCREEN,
                                    arguments = mapOf(Pair("musicId", it))
                                )
                            )
                        },
                        navigateBack = {
                            colorThemeManager.removePlaylistTheme()
                            navigationController.navigateBack()
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
            ),
            Screen(
                screenRoute = RoutesNames.SELECTED_ARTIST_SCREEN,
                screen = {
                    SelectedArtistScreenView(
                        navigateToModifyArtist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_ARTIST_SCREEN,
                                    arguments = mapOf(Pair("artistId", it))
                                )
                            )
                        },
                        selectedArtistId = navigationController.getArgument("artistId") as String?
                            ?: "",
                        playlistState = playlistState,
                        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                        navigateToModifyMusic = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_MUSIC_SCREEN,
                                    arguments = mapOf(Pair("musicId", it))
                                )
                            )
                        },
                        navigateBack = {
                            colorThemeManager.removePlaylistTheme()
                            navigationController.navigateBack()
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
            ),
            Screen(
                screenRoute = RoutesNames.MODIFY_PLAYLIST_SCREEN,
                screen ={
                    ModifyPlaylistScreenView(
                        modifyPlaylistViewModel = modifyPlaylistViewModel,
                        selectedPlaylistId = navigationController.getArgument("playlistId") as String? ?: "",
                        finishAction = { navigationController.navigateBack() }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MODIFY_MUSIC_SCREEN,
                screen = {
                    ModifyMusicScreenView(
                        selectedMusicId = navigationController.getArgument("musicId") as String? ?: "",
                        finishAction = { navigationController.navigateBack() },
                        modifyMusicViewModel = modifyMusicViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MODIFY_ALBUM_SCREEN,
                screen = {
                    ModifyAlbumScreenView(
                        selectedAlbumId = navigationController.getArgument("albumId") as String? ?: "",
                        finishAction = { navigationController.navigateBack() },
                        modifyAlbumViewModel = modifyAlbumViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MODIFY_ARTIST_SCREEN,
                screen = {
                    ModifyArtistScreenView(
                        selectedArtistId = navigationController.getArgument("artistId") as String? ?: "",
                        finishAction = { navigationController.navigateBack() },
                        modifyArtistViewModel = modifyArtistViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_SCREEN,
                screen = {
                    SettingsScreenView(
                        finishAction = { navigationController.navigateBack() },
                        navigateToColorTheme = {
                            navigationController.navigateTo(
                                Route(route = RoutesNames.SETTINGS_COLOR_THEME_SCREEN)
                            )
                        },
                        navigateToManageMusics = {
                            navigationController.navigateTo(
                                Route(route = RoutesNames.SETTINGS_MANAGE_MUSICS_SCREEN)
                            )
                        },
                        navigateToPersonalisation = {
                            navigationController.navigateTo(
                                Route(route = RoutesNames.SETTINGS_PERSONALISATION_SCREEN)
                            )
                        },
                        navigateToAbout = {
                            navigationController.navigateTo(
                                Route(route = RoutesNames.SETTINGS_ABOUT_SCREEN)
                            )
                        }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_PERSONALISATION_SCREEN,
                screen = {
                    SettingsPersonalisationScreenView(
                        finishAction = { navigationController.navigateBack() }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_MANAGE_MUSICS_SCREEN,
                screen = {
                    SettingsManageMusicsScreenView(
                        finishAction = { navigationController.navigateBack() },
                        navigateToFolders = {
                            allFoldersViewModel.handler.onFolderEvent(
                                FolderEvent.FetchFolders
                            )
                            navigationController.navigateTo(
                                Route(route = RoutesNames.SETTINGS_USED_FOLDERS_SCREEN)
                            )
                        },
                        navigateToAddMusics = {
                            addMusicsViewModel.handler.onAddMusicEvent(AddMusicsEvent.ResetState)
                            navigationController.navigateTo(
                                Route(route = RoutesNames.SETTINGS_ADD_MUSICS_SCREEN)
                            )
                        }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_USED_FOLDERS_SCREEN,
                screen = {
                    SettingsUsedFoldersScreenView(
                        finishAction = { navigationController.navigateBack() },
                        settingsAllFoldersViewModel = settingsAllFoldersViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_ADD_MUSICS_SCREEN,
                screen = {
                    SettingsAddMusicsScreenView(
                        finishAction = { navigationController.navigateBack() },
                        saveMusicFunction = addMusicsViewModel.handler::addMusic,
                        addMusicsViewModel = addMusicsViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_COLOR_THEME_SCREEN,
                screen = {
                    SettingsColorThemeScreenView(
                        finishAction = { navigationController.navigateBack() }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_ABOUT_SCREEN,
                screen = {
                    SettingsAboutScreenView(
                        finishAction = { navigationController.navigateBack() },
                        navigateToDevelopers = { navigationController.navigateBack() }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_DEVELOPERS_SCREEN,
                screen = {
                    SettingsDevelopersScreenView(
                        finishAction = { navigationController.navigateBack() }
                    )
                }
            )
        )
    )

//    NavHost(
//        modifier = Modifier.fillMaxSize(),
//        navController = navController,
//        startDestination = "mainPage",
//        enterTransition =
//        { fadeIn(animationSpec = tween(Constants.AnimationDuration.normal)) },
//        exitTransition =
//        { fadeOut(animationSpec = tween(Constants.AnimationDuration.normal)) },
//    ) {
//        composable("mainPage") {
//            MainPageScreen(
//                navigateToPlaylist = {
//                    navController.navigate("selectedPlaylist/$it")
//                },
//                navigateToAlbum = {
//                    navController.navigate("selectedAlbum/$it")
//                },
//                navigateToArtist = {
//                    navController.navigate("selectedArtist/$it")
//                },
//                navigateToMorePlaylist = {
//                    navController.navigate("morePlaylists")
//                },
//                navigateToMoreArtists = {
//                    navController.navigate("moreArtists")
//                },
//                navigateToMoreShortcuts = {
//                    navController.navigate("moreShortcuts")
//                },
//                navigateToMoreAlbums = {
//                    navController.navigate("moreAlbums")
//                },
//                navigateToModifyMusic = {
//                    navController.navigate("modifyMusic/$it")
//                },
//                navigateToModifyPlaylist = {
//                    navController.navigate("modifyPlaylist/$it")
//                },
//                navigateToModifyAlbum = {
//                    navController.navigate("modifyAlbum/$it")
//                },
//                navigateToModifyArtist = {
//                    navController.navigate("modifyArtist/$it")
//                },
//                navigateToSettings = {
//                    navController.navigate("settings")
//                },
//                playerDraggableState = playerDraggableState,
//                searchDraggableState = searchDraggableState,
//                musicState = musicState,
//                playlistState = playlistState,
//                albumState = albumState,
//                artistState = artistState,
//                quickAccessState = quickAccessState,
//                allAlbumsViewModel = allAlbumsViewModel,
//                allMusicsViewModel = allMusicsViewModel,
//                allPlaylistsViewModel = allPlaylistsViewModel,
//                allArtistsViewModel = allArtistsViewModel,
//                allImageCoversViewModel = allImageCoversViewModel,
//                playerMusicListViewModel = playerMusicListViewModel
//            )
//        }
//        composable(
//            "selectedPlaylist/{playlistId}",
//            arguments = listOf(navArgument("playlistId") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            SelectedPlaylistScreen(
//                navigateToModifyPlaylist = {
//                    navController.navigate(
//                        "modifyPlaylist/" + backStackEntry.arguments?.getString(
//                            "playlistId"
//                        )
//                    )
//                },
//                selectedPlaylistId = backStackEntry.arguments?.getString("playlistId")!!,
//                playlistState = playlistState,
//                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
//                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
//                navigateBack = {
//                    colorThemeManager.removePlaylistTheme()
//                    navController.popBackStack()
//                },
//                retrieveCoverMethod = {
//                    allImageCoversViewModel.handler.getImageCover(
//                        it
//                    )
//                },
//                playerDraggableState = playerDraggableState,
//                selectedPlaylistViewModel = selectedPlaylistViewModel,
//                playerMusicListViewModel = playerMusicListViewModel
//            )
//        }
//        composable(
//            "selectedAlbum/{albumId}",
//            arguments = listOf(navArgument("albumId") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            SelectedAlbumScreen(
//                navigateToModifyAlbum = {
//                    navController.navigate(
//                        "modifyAlbum/" + backStackEntry.arguments?.getString(
//                            "albumId"
//                        )
//                    )
//                },
//                selectedAlbumId = backStackEntry.arguments?.getString("albumId")!!,
//                playlistState = playlistState,
//                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
//                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
//                navigateBack = {
//                    colorThemeManager.removePlaylistTheme()
//                    navController.popBackStack()
//                },
//                retrieveCoverMethod = {
//                    allImageCoversViewModel.handler.getImageCover(
//                        it
//                    )
//                },
//                playerDraggableState = playerDraggableState,
//                playerMusicListViewModel = playerMusicListViewModel,
//                selectedAlbumViewModel = selectedAlbumViewModel
//            )
//        }
//        composable(
//            "selectedArtist/{artistId}",
//            arguments = listOf(navArgument("artistId") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            SelectedArtistScreen(
//                navigateToModifyArtist = {
//                    navController.navigate(
//                        "modifyArtist/" + backStackEntry.arguments?.getString(
//                            "artistId"
//                        )
//                    )
//                },
//                selectedArtistId = backStackEntry.arguments?.getString("artistId")!!,
//                playlistState = playlistState,
//                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
//                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
//                navigateBack = {
//                    colorThemeManager.removePlaylistTheme()
//                    navController.popBackStack()
//                },
//                retrieveCoverMethod = {
//                    allImageCoversViewModel.handler.getImageCover(
//                        it
//                    )
//                },
//                playerDraggableState = playerDraggableState,
//                playerMusicListViewModel = playerMusicListViewModel,
//                selectedArtistViewModel = selectedArtistViewModel
//            )
//        }
//        composable(
//            "modifyPlaylist/{playlistId}",
//            arguments = listOf(navArgument("playlistId") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            ModifyPlaylistScreen(
//                modifyPlaylistViewModel = modifyPlaylistViewModel,
//                selectedPlaylistId = backStackEntry.arguments?.getString("playlistId")!!,
//                finishAction = { navController.popBackStack() }
//            )
//        }
//        composable(
//            "modifyMusic/{musicId}",
//            arguments = listOf(navArgument("musicId") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            ModifyMusicScreen(
//                selectedMusicId = backStackEntry.arguments?.getString("musicId")!!,
//                finishAction = { navController.popBackStack() },
//                modifyMusicViewModel = modifyMusicViewModel
//            )
//        }
//        composable(
//            "modifyAlbum/{albumId}",
//            arguments = listOf(navArgument("albumId") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            ModifyAlbumScreen(
//                selectedAlbumId = backStackEntry.arguments?.getString("albumId")!!,
//                finishAction = { navController.popBackStack() },
//                modifyAlbumViewModel = modifyAlbumViewModel
//            )
//        }
//        composable(
//            "modifyArtist/{artistId}",
//            arguments = listOf(navArgument("artistId") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            ModifyArtistScreen(
//                selectedArtistId = backStackEntry.arguments?.getString("artistId")!!,
//                finishAction = { navController.popBackStack() },
//                modifyArtistViewModel = modifyArtistViewModel
//            )
//        }
//        composable(
//            "morePlaylists"
//        ) {
//            MorePlaylistsScreen(
//                navigateToSelectedPlaylist = { navController.navigate("selectedPlaylist/$it") },
//                finishAction = { navController.popBackStack() },
//                navigateToModifyPlaylist = { navController.navigate("modifyPlaylist/$it") },
//                retrieveCoverMethod = {
//                    allImageCoversViewModel.handler.getImageCover(
//                        it
//                    )
//                },
//                allPlaylistsViewModel = allPlaylistsViewModel
//            )
//        }
//        composable(
//            "moreAlbums"
//        ) {
//            MoreAlbumsScreen(
//                navigateToSelectedAlbum = { navController.navigate("selectedAlbum/$it") },
//                finishAction = { navController.popBackStack() },
//                navigateToModifyAlbum = { navController.navigate("modifyAlbum/$it") },
//                retrieveCoverMethod = {
//                    allImageCoversViewModel.handler.getImageCover(
//                        it
//                    )
//                },
//                allAlbumsViewModel = allAlbumsViewModel
//            )
//        }
//        composable(
//            "moreArtists"
//        ) {
//            MoreArtistsScreen(
//                navigateToSelectedArtist = { navController.navigate("selectedArtist/$it") },
//                finishAction = { navController.popBackStack() },
//                navigateToModifyArtist = { navController.navigate("modifyArtist/$it") },
//                retrieveCoverMethod = {
//                    allImageCoversViewModel.handler.getImageCover(
//                        it
//                    )
//                },
//                allArtistsViewModel = allArtistsViewModel
//            )
//        }
//        composable(
//            "settings"
//        ) {
//            SettingsScreen(
//                finishAction = { navController.popBackStack() },
//                navigateToColorTheme = {
//                    navController.navigate("colorTheme")
//                },
//                navigateToManageMusics = {
//                    navController.navigate("manageMusics")
//                },
//                navigateToPersonalisation = {
//                    navController.navigate("personalisation")
//                },
//                navigateToAbout = {
//                    navController.navigate("about")
//                }
//            )
//        }
//        composable(
//            "personalisation"
//        ) {
//            SettingsPersonalisationScreen(
//                finishAction = { navController.popBackStack() }
//            )
//        }
//        composable(
//            "manageMusics"
//        ) {
//            SettingsManageMusicsScreen(
//                finishAction = { navController.popBackStack() },
//                navigateToFolders = {
//                    allFoldersViewModel.handler.onFolderEvent(
//                        FolderEvent.FetchFolders
//                    )
//                    navController.navigate("usedFolders")
//                },
//                navigateToAddMusics = {
//                    addMusicsViewModel.handler.onAddMusicEvent(AddMusicsEvent.ResetState)
//                    navController.navigate("addMusics")
//                }
//            )
//        }
//        composable(
//            "usedFolders"
//        ) {
//            SettingsUsedFoldersScreen(
//                finishAction = { navController.popBackStack() },
//                settingsAllFoldersViewModel = settingsAllFoldersViewModel
//            )
//        }
//        composable(
//            "addMusics"
//        ) {
//            SettingsAddMusicsScreen(
//                finishAction = { navController.popBackStack() },
//                saveMusicFunction = allMusicsViewModel.handler::addMusic,
//                addMusicsViewModel = addMusicsViewModel
//            )
//        }
//        composable(
//            "colorTheme"
//        ) {
//            SettingsColorThemeScreen(
//                finishAction = { navController.popBackStack() },
//                updateColorThemeMethod = colorThemeManager::updateColorTheme
//            )
//        }
//        composable(
//            "about"
//        ) {
//            SettingsAboutScreen(
//                finishAction = { navController.popBackStack() },
//                navigateToDevelopers = { navController.navigate("developers") }
//            )
//        }
//        composable(
//            "developers"
//        ) {
//            SettingsDevelopersScreen(
//                finishAction = { navController.popBackStack() }
//            )
//        }
//    }
}
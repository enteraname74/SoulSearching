package com.github.soulsearching.composables

import androidx.compose.runtime.Composable
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.draggablestates.PlayerDraggableState
import com.github.soulsearching.draggablestates.PlayerMusicListDraggableState
import com.github.soulsearching.draggablestates.SearchDraggableState
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.navigation.NavigationController
import com.github.soulsearching.navigation.NavigationHost
import com.github.soulsearching.navigation.Route
import com.github.soulsearching.navigation.RoutesNames
import com.github.soulsearching.navigation.Screen
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
    colorThemeManager: ColorThemeManager = injectElement(),
    playerDraggableState: PlayerDraggableState,
    playerMusicListDraggableState: PlayerMusicListDraggableState,
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
    settingsAllFoldersViewModel: SettingsAllFoldersViewModel,
    navigationController: NavigationController<RoutesNames>
) {

    NavigationHost(
        playerDraggableState = playerDraggableState,
        playerMusicListDraggableState =  playerMusicListDraggableState,
        navigationController = navigationController,
        screens = listOf(
            Screen(
                screenRoute = RoutesNames.MAIN_PAGE_SCREEN,
                screen = {
                    MainPageScreen(
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
                        navigateToMorePlaylist = {
                            navigationController.navigateTo(
                                Route(route = RoutesNames.MORE_PLAYLISTS_SCREEN)
                            )
                        },
                        navigateToMoreArtists = {
                            navigationController.navigateTo(
                                Route(route = RoutesNames.MORE_ARTISTS_SCREEN)
                            )
                        },
                        navigateToMoreShortcuts = {
//                            navController.navigate("moreShortcuts")
                        },
                        navigateToMoreAlbums = {
                            navigationController.navigateTo(
                                Route(route = RoutesNames.MORE_ALBUMS_SCREEN)
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
                        quickAccessState = quickAccessState,
                        allAlbumsViewModel = allAlbumsViewModel,
                        allMusicsViewModel = allMusicsViewModel,
                        allPlaylistsViewModel = allPlaylistsViewModel,
                        allArtistsViewModel = allArtistsViewModel,
                        allImageCoversViewModel = allImageCoversViewModel,
                        playerMusicListViewModel = playerMusicListViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SELECTED_PLAYLIST_SCREEN,
                screen = {
                    SelectedPlaylistScreen(
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
                    SelectedAlbumScreen(
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
                    SelectedArtistScreen(
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
                    ModifyPlaylistScreen(
                        modifyPlaylistViewModel = modifyPlaylistViewModel,
                        selectedPlaylistId = navigationController.getArgument("playlistId") as String? ?: "",
                        finishAction = { navigationController.navigateBack() }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MODIFY_MUSIC_SCREEN,
                screen = {
                    ModifyMusicScreen(
                        selectedMusicId = navigationController.getArgument("musicId") as String? ?: "",
                        finishAction = { navigationController.navigateBack() },
                        modifyMusicViewModel = modifyMusicViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MODIFY_ALBUM_SCREEN,
                screen = {
                    ModifyAlbumScreen(
                        selectedAlbumId = navigationController.getArgument("albumId") as String? ?: "",
                        finishAction = { navigationController.navigateBack() },
                        modifyAlbumViewModel = modifyAlbumViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MODIFY_ARTIST_SCREEN,
                screen = {
                    ModifyArtistScreen(
                        selectedArtistId = navigationController.getArgument("artistId") as String? ?: "",
                        finishAction = { navigationController.navigateBack() },
                        modifyArtistViewModel = modifyArtistViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MORE_PLAYLISTS_SCREEN,
                screen = {
                    MorePlaylistsScreen(
                        navigateToSelectedPlaylist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.SELECTED_PLAYLIST_SCREEN,
                                    arguments = mapOf(Pair("playlistId", it))
                                )
                            )
                        },
                        finishAction = { navigationController.navigateBack() },
                        navigateToModifyPlaylist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_PLAYLIST_SCREEN,
                                    arguments = mapOf(Pair("playlistId", it))
                                )
                            )
                        },
                        retrieveCoverMethod = {
                            allImageCoversViewModel.handler.getImageCover(
                                it
                            )
                        },
                        allPlaylistsViewModel = allPlaylistsViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MORE_ALBUMS_SCREEN,
                screen = {
                    MoreAlbumsScreen(
                        navigateToSelectedAlbum = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.SELECTED_ALBUM_SCREEN,
                                    arguments = mapOf(Pair("albumId", it))
                                )
                            )
                        },
                        finishAction = { navigationController.navigateBack() },
                        navigateToModifyAlbum = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_ALBUM_SCREEN,
                                    arguments = mapOf(Pair("albumId", it))
                                )
                            )
                        },
                        retrieveCoverMethod = {
                            allImageCoversViewModel.handler.getImageCover(
                                it
                            )
                        },
                        allAlbumsViewModel = allAlbumsViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.MORE_ARTISTS_SCREEN,
                screen = {
                    MoreArtistsScreen(
                        navigateToSelectedArtist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.SELECTED_ARTIST_SCREEN,
                                    arguments = mapOf(Pair("artistId", it))
                                )
                            )
                        },
                        finishAction = { navigationController.navigateBack() },
                        navigateToModifyArtist = {
                            navigationController.navigateTo(
                                Route(
                                    route = RoutesNames.MODIFY_ARTIST_SCREEN,
                                    arguments = mapOf(Pair("artistId", it))
                                )
                            )
                        },
                        retrieveCoverMethod = {
                            allImageCoversViewModel.handler.getImageCover(
                                it
                            )
                        },
                        allArtistsViewModel = allArtistsViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_SCREEN,
                screen = {
                    SettingsScreen(
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
                    SettingsPersonalisationScreen(
                        finishAction = { navigationController.navigateBack() }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_MANAGE_MUSICS_SCREEN,
                screen = {
                    SettingsManageMusicsScreen(
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
                    SettingsUsedFoldersScreen(
                        finishAction = { navigationController.navigateBack() },
                        settingsAllFoldersViewModel = settingsAllFoldersViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_ADD_MUSICS_SCREEN,
                screen = {
                    SettingsAddMusicsScreen(
                        finishAction = { navigationController.navigateBack() },
                        saveMusicFunction = allMusicsViewModel.handler::addMusic,
                        addMusicsViewModel = addMusicsViewModel
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_COLOR_THEME_SCREEN,
                screen = {
                    SettingsColorThemeScreen(
                        finishAction = { navigationController.navigateBack() },
                        updateColorThemeMethod = colorThemeManager::updateColorTheme
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_ABOUT_SCREEN,
                screen = {
                    SettingsAboutScreen(
                        finishAction = { navigationController.navigateBack() },
                        navigateToDevelopers = { navigationController.navigateBack() }
                    )
                }
            ),
            Screen(
                screenRoute = RoutesNames.SETTINGS_DEVELOPERS_SCREEN,
                screen = {
                    SettingsDevelopersScreen(
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
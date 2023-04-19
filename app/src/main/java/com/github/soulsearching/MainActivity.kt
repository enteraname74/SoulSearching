package com.github.soulsearching

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.soulsearching.composables.*
import com.github.soulsearching.composables.bottomSheets.TestBottomSheet
import com.github.soulsearching.screens.*
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.*
import dagger.hilt.android.AndroidEntryPoint

@Suppress("UNCHECKED_CAST")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // Main page view models
    private val allMusicsViewModel: AllMusicsViewModel by viewModels()
    private val allPlaylistsViewModel: AllPlaylistsViewModel by viewModels()
    private val allAlbumsViewModel: AllAlbumsViewModel by viewModels()
    private val allArtistsViewModel: AllArtistsViewModel by viewModels()

    // Selected page view models
    private val selectedPlaylistViewModel: SelectedPlaylistViewModel by viewModels()
    private val selectedAlbumViewModel: SelectedAlbumViewModel by viewModels()
    private val selectedArtistsViewModel: SelectedArtistViewModel by viewModels()

    // Modify page view models
    private val modifyPlaylistViewModel: ModifyPlaylistViewModel by viewModels()
    private val modifyAlbumViewModel: ModifyAlbumViewModel by viewModels()
    private val modifyArtistViewModel: ModifyArtistViewModel by viewModels()
    private val modifyMusicViewModel: ModifyMusicViewModel by viewModels()

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoulSearchingTheme {

                val playlistState by allPlaylistsViewModel.state.collectAsState()

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val constraintsScope = this
                    val maxHeight = with(LocalDensity.current) {
                        constraintsScope.maxHeight.toPx()
                    }

                    NavHost(navController = navController, startDestination = "mainPage") {
                        composable("mainPage") {
                            MainPageScreen(
                                allMusicsViewModel = allMusicsViewModel,
                                allPlaylistsViewModel = allPlaylistsViewModel,
                                allAlbumsViewModel = allAlbumsViewModel,
                                allArtistsViewModel = allArtistsViewModel,
                                navigateToPlaylist = {
                                    navController.navigate("selectedPlaylist/$it")
                                },
                                navigateToAlbum = {
                                    navController.navigate("selectedAlbum/$it")
                                },
                                navigateToArtist = {
                                    navController.navigate("selectedArtist/$it")
                                },
                                navigateToMoreAlbums = {
                                    navController.navigate("moreAlbums")
                                },
                                navigateToMoreArtists = {
                                    navController.navigate("moreArtists")
                                },
                                navigateToMorePlaylist = {
                                    navController.navigate("morePlaylists")
                                },
                                navigateToMoreShortcuts = {
                                    navController.navigate("moreShortcuts")
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
                                }
                            )
                        }
                        composable(
                            "selectedPlaylist/{playlistId}",
                            arguments = listOf(navArgument("playlistId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            SelectedPlaylistScreen(
                                selectedPlaylistViewModel = selectedPlaylistViewModel,
                                navigateToModifyPlaylist = {
                                    navController.navigate(
                                        "modifyPlaylist/" + backStackEntry.arguments?.getString(
                                            "playlistId"
                                        )
                                    )
                                },
                                selectedPlaylistId = backStackEntry.arguments?.getString("playlistId")!!,
                                playlistState = playlistState,
                                onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                                navigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "selectedAlbum/{albumId}",
                            arguments = listOf(navArgument("albumId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            SelectedAlbumScreen(
                                selectedAlbumViewModel = selectedAlbumViewModel,
                                navigateToModifyAlbum = {
                                    navController.navigate(
                                        "modifyAlbum/" + backStackEntry.arguments?.getString(
                                            "albumId"
                                        )
                                    )
                                },
                                selectedAlbumId = backStackEntry.arguments?.getString("albumId")!!,
                                playlistState = playlistState,
                                onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                                navigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "selectedArtist/{artistId}",
                            arguments = listOf(navArgument("artistId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            SelectedArtistScreen(
                                selectedArtistViewModel = selectedArtistsViewModel,
                                navigateToModifyArtist = {
                                    navController.navigate(
                                        "modifyArtist/" + backStackEntry.arguments?.getString(
                                            "artistId"
                                        )
                                    )
                                },
                                selectedArtistId = backStackEntry.arguments?.getString("artistId")!!,
                                playlistState = playlistState,
                                onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                                navigateBack = { navController.popBackStack() }
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
                                modifyMusicViewModel = modifyMusicViewModel,
                                selectedMusicId = backStackEntry.arguments?.getString("musicId")!!,
                                finishAction = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "modifyAlbum/{albumId}",
                            arguments = listOf(navArgument("albumId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            ModifyAlbumScreen(
                                modifyAlbumViewModel = modifyAlbumViewModel,
                                selectedAlbumId = backStackEntry.arguments?.getString("albumId")!!,
                                finishAction = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "modifyArtist/{artistId}",
                            arguments = listOf(navArgument("artistId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            ModifyArtistScreen(
                                modifyArtistViewModel = modifyArtistViewModel,
                                selectedArtistId = backStackEntry.arguments?.getString("artistId")!!,
                                finishAction = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "morePlaylists"
                        ) {
                            MorePlaylistsScreen(
                                allPlaylistsViewModel = allPlaylistsViewModel,
                                navigateToSelectedPlaylist = { navController.navigate("selectedPlaylist/$it") },
                                finishAction = { navController.popBackStack() },
                                navigateToModifyPlaylist = { navController.navigate("modifyPlaylist/$it") }
                            )
                        }
                        composable(
                            "moreAlbums"
                        ) {
                            MoreAlbumsScreen(
                                allAlbumsViewModel = allAlbumsViewModel,
                                navigateToSelectedAlbum = { navController.navigate("selectedAlbum/$it") },
                                finishAction = { navController.popBackStack() },
                                navigateToModifyAlbum = { navController.navigate("modifyAlbum/$it") }
                            )
                        }
                        composable(
                            "moreArtists"
                        ) {
                            MoreArtistsScreen(
                                allArtistsViewModel = allArtistsViewModel,
                                navigateToSelectedArtist = { navController.navigate("selectedArtist/$it") },
                                finishAction = { navController.popBackStack() },
                                navigateToModifyArtist = { navController.navigate("modifyArtist/$it") }
                            )
                        }
                    }
                    TestBottomSheet(maxHeight = maxHeight)
                }
            }
        }
    }
}
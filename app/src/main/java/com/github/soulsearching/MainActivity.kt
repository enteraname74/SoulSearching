package com.github.soulsearching

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.composables.*
import com.github.soulsearching.composables.bottomSheets.TestBottomSheet
import com.github.soulsearching.screens.*
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Suppress("UNCHECKED_CAST")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // Main page view models
    private val allMusicsViewModel: AllMusicsViewModel by viewModels()
    private val allPlaylistsViewModel: AllPlaylistsViewModel by viewModels()
    private val allAlbumsViewModel: AllAlbumsViewModel by viewModels()
    private val allArtistsViewModel: AllArtistsViewModel by viewModels()
    private val allImageCoversViewModel: AllImageCoversViewModel by viewModels()

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

        SharedPrefUtils.sharedPreferences =
            getSharedPreferences(SharedPrefUtils.SHARED_PREF_KEY, Context.MODE_PRIVATE)
        SharedPrefUtils.initializeSorts(
            onMusicEvent = allMusicsViewModel::onMusicEvent,
            onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
            onArtistEvent = allArtistsViewModel::onArtistEvent,
            onAlbumEvent = allAlbumsViewModel::onAlbumEvent
        )

        setContent {
            SoulSearchingTheme {
                val playlistState by allPlaylistsViewModel.state.collectAsState()
                val coversState by allImageCoversViewModel.state.collectAsState()
                val context = LocalContext.current
                var isReadPermissionGranted by rememberSaveable {
                    mutableStateOf(false)
                }
                var hasMusicsBeenFetched by rememberSaveable {
                    mutableStateOf(SharedPrefUtils.hasMusicsBeenFetched())
                }

                // On regarde d'abord les permissions :
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    isReadPermissionGranted = isGranted
                }
                SideEffect {
                    if (Build.VERSION.SDK_INT >= 33) {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.READ_MEDIA_AUDIO
                            ) -> {
                                isReadPermissionGranted = true
                            }
                            else -> {
                                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_AUDIO)
                            }
                        }
                    } else {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                            ) -> {
                                isReadPermissionGranted = true
                            }
                            else -> {
                                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    }
                }

                if (isReadPermissionGranted) {
                    if (!hasMusicsBeenFetched) {
                        FetchingMusicsComposable(
                            finishAddingMusicsAction = {
                                SharedPrefUtils.setMusicsFetched()
                                hasMusicsBeenFetched = true
                            },
                            addingMusicAction = { music, cover ->
                                runBlocking {
                                    val job = CoroutineScope(Dispatchers.IO).launch {
                                        allMusicsViewModel.addMusic(music, cover)
                                    }
                                    job.join()
                                }
                            }
                        )
                    } else {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = MaterialTheme.colorScheme.primary)
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
                                        allImageCoversViewModel = allImageCoversViewModel,
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
                                        navigateBack = { navController.popBackStack() },
                                        coverList = coversState.covers
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
                                        navigateBack = { navController.popBackStack() },
                                        coverList = coversState.covers
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
                                        navigateBack = { navController.popBackStack() },
                                        coverList = coversState.covers
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
                                        finishAction = { navController.popBackStack() },
                                        coverState = coversState
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
                                        finishAction = { navController.popBackStack() },
                                        coverState = coversState
                                    )
                                }
                                composable(
                                    "morePlaylists"
                                ) {
                                    MorePlaylistsScreen(
                                        allPlaylistsViewModel = allPlaylistsViewModel,
                                        navigateToSelectedPlaylist = { navController.navigate("selectedPlaylist/$it") },
                                        finishAction = { navController.popBackStack() },
                                        navigateToModifyPlaylist = { navController.navigate("modifyPlaylist/$it") },
                                        coverList = coversState.covers
                                    )
                                }
                                composable(
                                    "moreAlbums"
                                ) {
                                    MoreAlbumsScreen(
                                        allAlbumsViewModel = allAlbumsViewModel,
                                        navigateToSelectedAlbum = { navController.navigate("selectedAlbum/$it") },
                                        finishAction = { navController.popBackStack() },
                                        navigateToModifyAlbum = { navController.navigate("modifyAlbum/$it") },
                                        coverList = coversState.covers
                                    )
                                }
                                composable(
                                    "moreArtists"
                                ) {
                                    MoreArtistsScreen(
                                        allArtistsViewModel = allArtistsViewModel,
                                        navigateToSelectedArtist = { navController.navigate("selectedArtist/$it") },
                                        finishAction = { navController.popBackStack() },
                                        navigateToModifyArtist = { navController.navigate("modifyArtist/$it") },
                                        coverList = coversState.covers
                                    )
                                }
                            }
                            TestBottomSheet(maxHeight = maxHeight)
                        }
                    }
                }
            }
        }
    }
}
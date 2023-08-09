package com.github.soulsearching

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
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
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.composables.*
import com.github.soulsearching.composables.bottomSheets.PlayerMusicListView
import com.github.soulsearching.composables.bottomSheets.PlayerSwipeableView
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.screens.*
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.service.notification.SoulSearchingNotificationService
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

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

    // PLayer view model :
    private val playerMusicListViewModel: PlayerMusicListViewModel by viewModels()

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterialApi::class)
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

        PlayerUtils.playerViewModel.retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) }

        setContent {
            SoulSearchingTheme {
                val playlistState by allPlaylistsViewModel.state.collectAsState()
                val coversState by allImageCoversViewModel.state.collectAsState()
                val playerMusicState by playerMusicListViewModel.state.collectAsState()

                val swipeableState = rememberSwipeableState(
                    BottomSheetStates.COLLAPSED
                )
                val musicListSwipeableState = rememberSwipeableState(
                    BottomSheetStates.COLLAPSED
                )
                val coroutineScope = rememberCoroutineScope()

                var hasPlayerMusicBeenFetched by rememberSaveable {
                    mutableStateOf(false)
                }

                if (!hasPlayerMusicBeenFetched) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playerSavedMusics = playerMusicListViewModel.getPlayerMusicList()
                        if (playerSavedMusics.isNotEmpty()) {
                            Log.d("MAIN ACTIVITY", "PLAYER LIST SIZE : ${playerSavedMusics.size}")
                            PlayerUtils.playerViewModel.setPlayerInformationsFromSavedList(playerSavedMusics)
                            launchService(isFromSavedList = true)
                            PlayerUtils.playerViewModel.shouldServiceBeLaunched = true
                            coroutineScope.launch{
                                swipeableState.animateTo(BottomSheetStates.MINIMISED, tween(300))
                            }
                        }
                    }
                    hasPlayerMusicBeenFetched = true
                }

                val context = LocalContext.current
                var isReadPermissionGranted by rememberSaveable {
                    mutableStateOf(false)
                }
                var isPostNotificationGranted by remember {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mutableStateOf(
                            ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        )
                    } else mutableStateOf(true)
                }
                var hasMusicsBeenFetched by rememberSaveable {
                    mutableStateOf(SharedPrefUtils.hasMusicsBeenFetched())
                }

                // On regarde d'abord les permissions :
                val readPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    isReadPermissionGranted = isGranted
                }
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    isPostNotificationGranted = isGranted
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
                                readPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_AUDIO)
                            }
                        }
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                            ) -> {
                                isReadPermissionGranted = true
                            }
                            else -> {
                                readPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    }
                }

                if (isReadPermissionGranted) {
                    if (!hasMusicsBeenFetched) {
                        Log.d("MAIN ACTIVITY", "FETCH MUSICS")
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
                            },
                            createFavoritePlaylistAction = {
                                allPlaylistsViewModel.onPlaylistEvent(
                                    PlaylistEvent.AddFavoritePlaylist(
                                        name = applicationContext.getString(R.string.favorite)
                                    )
                                )
                            }
                        )
                    } else {

                        if (PlayerUtils.playerViewModel.shouldServiceBeLaunched && !PlayerUtils.playerViewModel.isServiceLaunched) {
                            launchService(isFromSavedList = false)
                        }

                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = DynamicColor.primary)
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
                                        playerMusicListViewModel = playerMusicListViewModel,
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
                                        },
                                        swipeableState = swipeableState
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
                                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) },
                                        swipeableState = swipeableState,
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
                                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) },
                                        swipeableState = swipeableState,
                                        playerMusicListViewModel = playerMusicListViewModel
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
                                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) },
                                        swipeableState = swipeableState,
                                        playerMusicListViewModel = playerMusicListViewModel
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
                                        navigateToModifyPlaylist = { navController.navigate("modifyPlaylist/$it") },
                                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) }
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
                                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) }
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
                                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) }
                                    )
                                }
                            }
                            PlayerSwipeableView(
                                maxHeight = maxHeight,
                                swipeableState = swipeableState,
                                coverList = coversState.covers,
                                musicListSwipeableState = musicListSwipeableState,
                                playerMusicListViewModel = playerMusicListViewModel
                            )

                            PlayerMusicListView(
                                maxHeight = maxHeight,
                                swipeableState = swipeableState,
                                coverList = coversState.covers,
                                musicState = playerMusicState,
                                playlistState = playlistState,
                                onMusicEvent = playerMusicListViewModel::onMusicEvent,
                                onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                                navigateToModifyMusic = {
                                    navController.navigate("modifyMusic/$it")
                                },
                                musicListSwipeableState = musicListSwipeableState,
                                playerMusicListViewModel = playerMusicListViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    private fun launchService(isFromSavedList: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            val serviceIntent = Intent(this@MainActivity, PlayerService::class.java)
            serviceIntent.putExtra(PlayerService.IS_FROM_SAVED_LIST, isFromSavedList)
            startService(serviceIntent)
            PlayerUtils.playerViewModel.isServiceLaunched = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPrefUtils.setCurrentMusicPosition()
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(SoulSearchingNotificationService.CHANNEL_ID)
    }
}
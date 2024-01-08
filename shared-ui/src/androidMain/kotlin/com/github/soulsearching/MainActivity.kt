package com.github.soulsearching

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.soulsearching.classes.types.BottomSheetStates
import com.github.soulsearching.classes.utils.ColorPaletteUtils
import com.github.soulsearching.classes.utils.PlayerUtils
import com.github.soulsearching.classes.utils.SettingsUtils
import com.github.soulsearching.classes.utils.SharedPrefUtils
import com.github.soulsearching.classes.utils.Utils
import com.github.soulsearching.composables.FetchingMusicsComposable
import com.github.soulsearching.composables.MissingPermissionsComposable
import com.github.soulsearching.composables.player.PlayerDraggableView
import com.github.soulsearching.composables.player.PlayerMusicListView
import com.github.soulsearching.composables.remembers.checkIfPostNotificationGranted
import com.github.soulsearching.composables.remembers.checkIfReadPermissionGranted
import com.github.soulsearching.composables.remembers.rememberPlayerDraggableState
import com.github.soulsearching.composables.remembers.rememberPlayerMusicDraggableState
import com.github.soulsearching.composables.remembers.rememberSearchDraggableState
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.events.MusicEvent
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
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewmodel.AddMusicsViewModelImpl
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllFoldersViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.AllQuickAccessViewModel
import com.github.soulsearching.viewmodel.MainActivityViewModel
import com.github.soulsearching.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.viewmodel.ModifyArtistViewModel
import com.github.soulsearching.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.viewmodel.PlayerMusicListViewModelImpl
import com.github.soulsearching.viewmodel.PlayerViewModelImpl
import com.github.soulsearching.viewmodel.SelectedAlbumViewModel
import com.github.soulsearching.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import java.lang.RuntimeException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // Main page view models
    private lateinit var allMusicsViewModel: AllMusicsViewModel
    private lateinit var allPlaylistsViewModel: AllPlaylistsViewModel
    private lateinit var allAlbumsViewModel: AllAlbumsViewModel
    private lateinit var allArtistsViewModel: AllArtistsViewModel
    private lateinit var allImageCoversViewModel: AllImageCoversViewModel
    private lateinit var allQuickAccessViewModel: AllQuickAccessViewModel

    // Selected page view models
    private lateinit var selectedPlaylistViewModel: SelectedPlaylistViewModel
    private lateinit var selectedAlbumViewModel: SelectedAlbumViewModel
    private lateinit var selectedArtistsViewModel: SelectedArtistViewModel

    // Modify page view models
    private lateinit var modifyPlaylistViewModel: ModifyPlaylistViewModel
    private lateinit var modifyAlbumViewModel: ModifyAlbumViewModel
    private lateinit var modifyArtistViewModel: ModifyArtistViewModel
    private lateinit var modifyMusicViewModel: ModifyMusicViewModel

    // Player view model :
    lateinit var playerMusicListViewModel: PlayerMusicListViewModelImpl

    // Settings view models:
    private lateinit var allFoldersViewModel: AllFoldersViewModel
    private lateinit var addMusicsViewModel: AddMusicsViewModelImpl

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MAIN ACTIVITY", "SERVICE DIED, WILL RESTART")
            Utils.launchService(
                context = context,
                isFromSavedList = false
            )
        }
    }

    /**
     * Initialize the SharedPreferences.
     */
    private fun initializeSharedPreferences() {
        SharedPrefUtils.sharedPreferences =
            getSharedPreferences(SharedPrefUtils.SHARED_PREF_KEY, Context.MODE_PRIVATE)
        SharedPrefUtils.initializeSorts(
            onMusicEvent = allMusicsViewModel::onMusicEvent,
            onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
            onArtistEvent = allArtistsViewModel::onArtistEvent,
            onAlbumEvent = allAlbumsViewModel::onAlbumEvent
        )
        SharedPrefUtils.initializeSettings()
    }

    /**
     * Initialize the player ViewModel, used for managing view elements related to the playback.
     */
    private fun initializePlayerViewModel(
        playerViewModel: PlayerViewModelImpl
    ) {
        playerViewModel.context = applicationContext
        PlayerUtils.playerViewModel = playerViewModel
        PlayerUtils.playerViewModel.retrieveCoverMethod = allImageCoversViewModel::getImageCover
        PlayerUtils.playerViewModel.updateNbPlayed =
            { allMusicsViewModel.onMusicEvent(MusicEvent.AddNbPlayed(it)) }
    }

    /**
     * Initialize the broadcast receiver, used by the foreground service handling the playback.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initializeBroadcastReceive() {
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(
                serviceReceiver, IntentFilter(PlayerService.RESTART_SERVICE),
                RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(serviceReceiver, IntentFilter(PlayerService.RESTART_SERVICE))
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition", "UnspecifiedRegisterReceiverFlag")
    @OptIn(ExperimentalFoundationApi::class)
    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Main page view models
            allMusicsViewModel = koinViewModel()
            allPlaylistsViewModel = koinViewModel()
            allAlbumsViewModel = koinViewModel()
            allArtistsViewModel = koinViewModel()
            allImageCoversViewModel = koinViewModel()
            allQuickAccessViewModel = koinViewModel()

            // Selected page view models
            selectedPlaylistViewModel = koinViewModel()
            selectedAlbumViewModel = koinViewModel()
            selectedArtistsViewModel = koinViewModel()

            // Modify page view models
            modifyPlaylistViewModel = koinViewModel()
            modifyAlbumViewModel = koinViewModel()
            modifyArtistViewModel = koinViewModel()
            modifyMusicViewModel = koinViewModel()

            // Player view model :
            val playerViewModel: PlayerViewModelImpl = get()
            playerMusicListViewModel = koinViewModel()

            // Settings view models:
            allFoldersViewModel = koinViewModel()
            addMusicsViewModel = koinViewModel()

            initializeSharedPreferences()
            initializePlayerViewModel(playerViewModel)
            initializeBroadcastReceive()

            mainActivityViewModel = koinViewModel<MainActivityViewModel>()
            InitializeMainActivityViewModel()

            SoulSearchingTheme {
                val playlistState by allPlaylistsViewModel.state.collectAsState()
                val albumState by allAlbumsViewModel.state.collectAsState()
                val artistState by allArtistsViewModel.state.collectAsState()
                val musicState by allMusicsViewModel.state.collectAsState()
                val coversState by allImageCoversViewModel.state.collectAsState()
                val playerMusicListState by playerMusicListViewModel.state.collectAsState()
                val playerMusicState by PlayerUtils.playerViewModel.state.collectAsState()
                val quickAccessState by allQuickAccessViewModel.state.collectAsState()

                val coroutineScope = rememberCoroutineScope()

                PlayerUtils.playerViewModel.currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
                    image = PlayerUtils.playerViewModel.currentMusicCover
                )

                val readPermissionLauncher = permissionLauncher { isGranted ->
                    mainActivityViewModel.isReadPermissionGranted = isGranted
                }

                val postNotificationLauncher = permissionLauncher { isGranted ->
                    mainActivityViewModel.isPostNotificationGranted = isGranted
                }
                SideEffect {
                    checkAndAskMissingPermissions(
                        isReadPermissionGranted = mainActivityViewModel.isReadPermissionGranted,
                        isPostNotificationGranted = mainActivityViewModel.isPostNotificationGranted,
                        readPermissionLauncher = readPermissionLauncher,
                        postNotificationLauncher = postNotificationLauncher
                    )
                }
                if (!mainActivityViewModel.isReadPermissionGranted || !mainActivityViewModel.isPostNotificationGranted) {
                    MissingPermissionsComposable()
                    return@SoulSearchingTheme
                }
                if (!mainActivityViewModel.hasMusicsBeenFetched) {
                    FetchingMusicsComposable(
                        finishAddingMusicsAction = {
                            SharedPrefUtils.setMusicsFetched()
                            mainActivityViewModel.hasMusicsBeenFetched = true
                        },
                        allMusicsViewModel = allMusicsViewModel
                    )
                    return@SoulSearchingTheme
                }

                if (coversState.covers.isNotEmpty() && !mainActivityViewModel.cleanImagesLaunched) {
                    LaunchedEffect(key1 = "Launch") {
                        CoroutineScope(Dispatchers.IO).launch {
                            for (cover in coversState.covers) {
                                allImageCoversViewModel.deleteImageIsNotUsed(cover)
                            }
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            if (PlayerUtils.playerViewModel.currentMusic != null) {
                                PlayerUtils.playerViewModel.defineCoverAndPaletteFromCoverId(
                                    coverId = PlayerUtils.playerViewModel.currentMusic?.coverId
                                )
                                PlayerService.updateNotification()
                            }
                        }
                        mainActivityViewModel.cleanImagesLaunched = true
                    }
                }

                if (musicState.musics.isNotEmpty() && !mainActivityViewModel.cleanMusicsLaunched) {
                    allMusicsViewModel.checkAndDeleteMusicIfNotExist(applicationContext)
                    mainActivityViewModel.cleanMusicsLaunched = true
                }

                if (PlayerUtils.playerViewModel.shouldServiceBeLaunched && !PlayerUtils.playerViewModel.isServiceLaunched) {
                    Utils.launchService(
                        context = this@MainActivity,
                        isFromSavedList = false
                    )
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val constraintsScope = this
                    val maxHeight = with(LocalDensity.current) {
                        constraintsScope.maxHeight.toPx()
                    }

                    val playerDraggableState = rememberPlayerDraggableState(
                        constraintsScope = constraintsScope
                    )

                    val musicListDraggableState = rememberPlayerMusicDraggableState(
                        constraintsScope = constraintsScope
                    )
                    val searchDraggableState = rememberSearchDraggableState(
                        constraintsScope = constraintsScope
                    )

                    if (!mainActivityViewModel.hasLastPlayedMusicsBeenFetched) {
                        LaunchedEffect(key1 = "FETCH_LAST_PLAYED_LIST") {
                            val playerSavedMusics =
                                playerMusicListViewModel.getPlayerMusicList()
                            if (playerSavedMusics.isNotEmpty()) {
                                PlayerUtils.playerViewModel.setPlayerInformationFromSavedList(
                                    playerSavedMusics
                                )
                                Utils.launchService(
                                    context = this@MainActivity,
                                    isFromSavedList = true
                                )
                                PlayerUtils.playerViewModel.shouldServiceBeLaunched = true
                                coroutineScope.launch {
                                    playerDraggableState.state.animateTo(BottomSheetStates.MINIMISED)
                                }
                            }
                            mainActivityViewModel.hasLastPlayedMusicsBeenFetched = true
                        }
                    }

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
                                quickAccessState = quickAccessState
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
                                navigateBack = {
                                    SettingsUtils.settingsViewModel.setPlaylistCover(
                                        null
                                    )
                                    SettingsUtils.settingsViewModel.forceBasicThemeForPlaylists =
                                        false
                                    navController.popBackStack()
                                },
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.getImageCover(
                                        it
                                    )
                                },
                                playerDraggableState = playerDraggableState,
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
                                navigateBack = {
                                    SettingsUtils.settingsViewModel.setPlaylistCover(
                                        null
                                    )
                                    SettingsUtils.settingsViewModel.forceBasicThemeForPlaylists =
                                        false
                                    navController.popBackStack()
                                },
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.getImageCover(
                                        it
                                    )
                                },
                                playerDraggableState = playerDraggableState,
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
                                navigateBack = {
                                    SettingsUtils.settingsViewModel.setPlaylistCover(
                                        null
                                    )
                                    SettingsUtils.settingsViewModel.forceBasicThemeForPlaylists =
                                        false
                                    navController.popBackStack()
                                },
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.getImageCover(
                                        it
                                    )
                                },
                                playerDraggableState = playerDraggableState,
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
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.getImageCover(
                                        it
                                    )
                                }
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
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.getImageCover(
                                        it
                                    )
                                }
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
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.getImageCover(
                                        it
                                    )
                                }
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
                                    allFoldersViewModel.onFolderEvent(
                                        FolderEvent.FetchFolders
                                    )
                                    navController.navigate("usedFolders")
                                },
                                navigateToAddMusics = {
                                    addMusicsViewModel.onAddMusicEvent(AddMusicsEvent.ResetState)
                                    navController.navigate("addMusics")
                                }
                            )
                        }
                        composable(
                            "usedFolders"
                        ) {
                            SettingsUsedFoldersScreen(
                                finishAction = { navController.popBackStack() },
                                allFoldersViewModel = allFoldersViewModel
                            )
                        }
                        composable(
                            "addMusics"
                        ) {
                            SettingsAddMusicsScreen(
                                addMusicsViewModel = addMusicsViewModel,
                                finishAction = { navController.popBackStack() },
                                saveMusicFunction = allMusicsViewModel::addMusic
                            )
                        }
                        composable(
                            "colorTheme"
                        ) {
                            SettingsColorThemeScreen(
                                finishAction = { navController.popBackStack() }
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
                    PlayerDraggableView(
                        maxHeight = maxHeight,
                        draggableState = playerDraggableState,
                        retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                        musicListDraggableState = musicListDraggableState,
                        playerMusicListViewModel = playerMusicListViewModel,
                        onMusicEvent = PlayerUtils.playerViewModel::onMusicEvent,
                        isMusicInFavoriteMethod = allMusicsViewModel::isMusicInFavorite,
                        navigateToArtist = {
                            navController.navigate("selectedArtist/$it")
                        },
                        navigateToAlbum = {
                            navController.navigate("selectedAlbum/$it")
                        },
                        retrieveAlbumIdMethod = {
                            allMusicsViewModel.getAlbumIdFromMusicId(it)
                        },
                        retrieveArtistIdMethod = {
                            allMusicsViewModel.getArtistIdFromMusicId(it)
                        },
                        musicState = playerMusicState,
                        playlistState = playlistState,
                        onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                        navigateToModifyMusic = {
                            navController.navigate("modifyMusic/$it")
                        }
                    )

                    PlayerMusicListView(
                        coverList = coversState.covers,
                        musicState = playerMusicListState,
                        playlistState = playlistState,
                        onMusicEvent = playerMusicListViewModel::onMusicEvent,
                        onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                        navigateToModifyMusic = {
                            navController.navigate("modifyMusic/$it")
                        },
                        musicListDraggableState = musicListDraggableState,
                        playerDraggableState = playerDraggableState,
                        playerMusicListViewModel = playerMusicListViewModel
                    )
                }
            }
        }
    }

    /**
     * Initialize remaining elements of the MainActivityViewModel.
     * It sets the permissions states.
     */
    @Composable
    private fun InitializeMainActivityViewModel() {
        mainActivityViewModel.isReadPermissionGranted = checkIfReadPermissionGranted()
        mainActivityViewModel.isPostNotificationGranted = checkIfPostNotificationGranted()
    }

    /**
     * Build a permission launcher.
     */
    @Composable
    private fun permissionLauncher(
        onResult: (Boolean) -> Unit
    ): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onResult(isGranted)
        }
    }

    /**
     * Check and ask for missing permissions.
     */
    private fun checkAndAskMissingPermissions(
        isReadPermissionGranted: Boolean,
        isPostNotificationGranted: Boolean,
        readPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
        postNotificationLauncher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        if (!isReadPermissionGranted) {
            readPermissionLauncher.launch(
                if (Build.VERSION.SDK_INT >= 33) {
                    android.Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                }
            )
        }

        if (!isPostNotificationGranted && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) {
            postNotificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            allMusicsViewModel.checkAndDeleteMusicIfNotExist(applicationContext)
        } catch (_:RuntimeException) {

        }
    }
}
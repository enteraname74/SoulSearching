package com.github.soulsearching

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
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
import com.github.soulsearching.classes.utils.AndroidUtils
import com.github.soulsearching.composables.MissingPermissionsComposable
import com.github.soulsearching.composables.appfeatures.FetchingMusicsComposable
import com.github.soulsearching.composables.player.PlayerDraggableView
import com.github.soulsearching.composables.player.PlayerMusicListView
import com.github.soulsearching.composables.remembers.rememberPlayerDraggableState
import com.github.soulsearching.composables.remembers.rememberPlayerMusicDraggableState
import com.github.soulsearching.composables.remembers.rememberSearchDraggableState
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.playback.PlayerService
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
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.utils.SettingsUtils
import com.github.soulsearching.viewmodel.AddMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllAlbumsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllArtistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllFoldersViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllImageCoversViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllPlaylistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllQuickAccessViewModelAndroidImpl
import com.github.soulsearching.viewmodel.MainActivityViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyAlbumViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyArtistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyMusicViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerMusicListViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedAlbumViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedArtistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {
    // Main page view models
    private lateinit var allMusicsViewModel: AllMusicsViewModelAndroidImpl
    private lateinit var allPlaylistsViewModel: AllPlaylistsViewModelAndroidImpl
    private lateinit var allAlbumsViewModel: AllAlbumsViewModelAndroidImpl
    private lateinit var allArtistsViewModel: AllArtistsViewModelAndroidImpl
    private lateinit var allImageCoversViewModel: AllImageCoversViewModelAndroidImpl
    private lateinit var allQuickAccessViewModel: AllQuickAccessViewModelAndroidImpl

    // Selected page view models
    private lateinit var selectedPlaylistViewModel: SelectedPlaylistViewModelAndroidImpl
    private lateinit var selectedAlbumViewModel: SelectedAlbumViewModelAndroidImpl
    private lateinit var selectedArtistsViewModel: SelectedArtistViewModelAndroidImpl

    // Modify page view models
    private lateinit var modifyPlaylistViewModel: ModifyPlaylistViewModelAndroidImpl
    private lateinit var modifyAlbumViewModel: ModifyAlbumViewModelAndroidImpl
    private lateinit var modifyArtistViewModel: ModifyArtistViewModelAndroidImpl
    private lateinit var modifyMusicViewModel: ModifyMusicViewModelAndroidImpl

    // Player view model :
    private lateinit var playerViewModel: PlayerViewModelAndroidImpl
    private lateinit var playerMusicListViewModel: PlayerMusicListViewModelAndroidImpl

    // Settings view models:
    private lateinit var allFoldersViewModel: AllFoldersViewModelAndroidImpl
    private lateinit var addMusicsViewModel: AddMusicsViewModelAndroidImpl

    private lateinit var mainActivityViewModel: MainActivityViewModelAndroidImpl

    private val settings: SoulSearchingSettings by inject<SoulSearchingSettings>()
    private val playbackManager: PlaybackManagerAndroidImpl by inject<PlaybackManagerAndroidImpl>()
    private val colorThemeManager: ColorThemeManager by inject<ColorThemeManager>()

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            AndroidUtils.launchService(
                context = context,
                isFromSavedList = false
            )
        }
    }

    /**
     * Initialize the SharedPreferences.
     */
    private fun initializeSharedPreferences() {
        settings.initializeSorts(
            onMusicEvent = allMusicsViewModel.handler::onMusicEvent,
            onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
            onArtistEvent = allArtistsViewModel.handler::onArtistEvent,
            onAlbumEvent = allAlbumsViewModel.handler::onAlbumEvent
        )
    }

    /**
     * Initialize the player ViewModel, used for managing view elements related to the playback.
     */
    private fun initializePlayerViewModel(){
        PlayerUtils.playerViewModel = playerViewModel
        PlayerUtils.playerViewModel.handler.retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover
        PlayerUtils.playerViewModel.handler.updateNbPlayed =
            { allMusicsViewModel.handler.onMusicEvent(MusicEvent.AddNbPlayed(it)) }
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
            SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()

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
            playerViewModel = koinViewModel()
            playerMusicListViewModel = koinViewModel()

            // Settings view models:
            allFoldersViewModel = koinViewModel()
            addMusicsViewModel = koinViewModel()
            SettingsUtils.settingsViewModel = inject<SettingsViewModel>().value

            initializeSharedPreferences()
            initializePlayerViewModel()
            initializeBroadcastReceive()

            mainActivityViewModel = koinViewModel()
            InitializeMainActivityViewModel()

            SoulSearchingTheme {
                val playlistState by allPlaylistsViewModel.handler.state.collectAsState()
                val albumState by allAlbumsViewModel.handler.state.collectAsState()
                val artistState by allArtistsViewModel.handler.state.collectAsState()
                val musicState by allMusicsViewModel.handler.state.collectAsState()
                val coversState by allImageCoversViewModel.handler.state.collectAsState()
                val playerMusicListState by playerMusicListViewModel.handler.state.collectAsState()
                val playerMusicState by PlayerUtils.playerViewModel.handler.state.collectAsState()
                val quickAccessState by allQuickAccessViewModel.handler.state.collectAsState()

                val coroutineScope = rememberCoroutineScope()

                colorThemeManager.currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
                    image = PlayerUtils.playerViewModel.handler.currentMusicCover
                )

                val readPermissionLauncher = permissionLauncher { isGranted ->
                    mainActivityViewModel.handler.isReadPermissionGranted = isGranted
                }

                val postNotificationLauncher = permissionLauncher { isGranted ->
                    mainActivityViewModel.handler.isPostNotificationGranted = isGranted
                }
                SideEffect {
                    checkAndAskMissingPermissions(
                        isReadPermissionGranted = mainActivityViewModel.handler.isReadPermissionGranted,
                        isPostNotificationGranted = mainActivityViewModel.handler.isPostNotificationGranted,
                        readPermissionLauncher = readPermissionLauncher,
                        postNotificationLauncher = postNotificationLauncher
                    )
                }
                if (!mainActivityViewModel.handler.isReadPermissionGranted || !mainActivityViewModel.handler.isPostNotificationGranted) {
                    MissingPermissionsComposable()
                    return@SoulSearchingTheme
                }
                if (!mainActivityViewModel.handler.hasMusicsBeenFetched) {
                    FetchingMusicsComposable(
                        finishAddingMusicsAction = {
                            settings.setBoolean(SoulSearchingSettings.HAS_MUSICS_BEEN_FETCHED_KEY, true)
                            mainActivityViewModel.handler.hasMusicsBeenFetched = true
                        },
                        allMusicsViewModel = allMusicsViewModel
                    )
                    return@SoulSearchingTheme
                }

                if (coversState.covers.isNotEmpty() && !mainActivityViewModel.handler.cleanImagesLaunched) {
                    LaunchedEffect(key1 = "Launch") {
                        CoroutineScope(Dispatchers.IO).launch {
                            for (cover in coversState.covers) {
                                allImageCoversViewModel.handler.deleteImageIsNotUsed(cover)
                            }
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            if (PlayerUtils.playerViewModel.handler.currentMusic != null) {
                                PlayerUtils.playerViewModel.handler.defineCoverAndPaletteFromCoverId(
                                    coverId = PlayerUtils.playerViewModel.handler.currentMusic?.coverId
                                )
                                playbackManager.updateNotification()
                            }
                        }
                        mainActivityViewModel.handler.cleanImagesLaunched = true
                    }
                }

                if (musicState.musics.isNotEmpty() && !mainActivityViewModel.handler.cleanMusicsLaunched) {
                    allMusicsViewModel.handler.checkAndDeleteMusicIfNotExist()
                    mainActivityViewModel.handler.cleanMusicsLaunched = true
                }

                if (PlayerUtils.playerViewModel.handler.shouldServiceBeLaunched && !PlayerUtils.playerViewModel.handler.isServiceLaunched) {
                    AndroidUtils.launchService(
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

                    if (!mainActivityViewModel.handler.hasLastPlayedMusicsBeenFetched) {
                        LaunchedEffect(key1 = "FETCH_LAST_PLAYED_LIST") {
                            val playerSavedMusics =
                                playerMusicListViewModel.handler.getPlayerMusicList()
                            if (playerSavedMusics.isNotEmpty()) {
                                PlayerUtils.playerViewModel.handler.setPlayerInformationFromSavedList(
                                    playerSavedMusics
                                )
                                AndroidUtils.launchService(
                                    context = this@MainActivity,
                                    isFromSavedList = true
                                )
                                PlayerUtils.playerViewModel.handler.shouldServiceBeLaunched = true
                                coroutineScope.launch {
                                    playerDraggableState.state.animateTo(BottomSheetStates.MINIMISED)
                                }
                            }
                            mainActivityViewModel.handler.hasLastPlayedMusicsBeenFetched = true
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
                                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                                navigateBack = {
                                    colorThemeManager.setNewPlaylistCover(null)
                                    colorThemeManager.forceBasicThemeForPlaylists = false
                                    navController.popBackStack()
                                },
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.handler.getImageCover(
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
                                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                                navigateBack = {
                                    colorThemeManager.setNewPlaylistCover(null)
                                    colorThemeManager.forceBasicThemeForPlaylists = false
                                    navController.popBackStack()
                                },
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.handler.getImageCover(
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
                                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                                navigateToModifyMusic = { navController.navigate("modifyMusic/$it") },
                                navigateBack = {
                                    colorThemeManager.setNewPlaylistCover(null)
                                    colorThemeManager.forceBasicThemeForPlaylists = false
                                    navController.popBackStack()
                                },
                                retrieveCoverMethod = {
                                    allImageCoversViewModel.handler.getImageCover(
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
                                    allImageCoversViewModel.handler.getImageCover(
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
                                    allImageCoversViewModel.handler.getImageCover(
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
                                    allImageCoversViewModel.handler.getImageCover(
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
                                allFoldersViewModel = allFoldersViewModel
                            )
                        }
                        composable(
                            "addMusics"
                        ) {
                            SettingsAddMusicsScreen(
                                addMusicsViewModel = addMusicsViewModel,
                                finishAction = { navController.popBackStack() },
                                saveMusicFunction = allMusicsViewModel.handler::addMusic
                            )
                        }
                        composable(
                            "colorTheme"
                        ) {
                            SettingsColorThemeScreen(
                                finishAction = { navController.popBackStack() },
                                updateColorThemeMethod = SettingsUtils.settingsViewModel.handler::updateColorTheme
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
                        retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                        musicListDraggableState = musicListDraggableState,
                        playerMusicListViewModel = playerMusicListViewModel,
                        onMusicEvent = PlayerUtils.playerViewModel.handler::onMusicEvent,
                        isMusicInFavoriteMethod = allMusicsViewModel.handler::isMusicInFavorite,
                        navigateToArtist = {
                            navController.navigate("selectedArtist/$it")
                        },
                        navigateToAlbum = {
                            navController.navigate("selectedAlbum/$it")
                        },
                        retrieveAlbumIdMethod = {
                            allMusicsViewModel.handler.getAlbumIdFromMusicId(it)
                        },
                        retrieveArtistIdMethod = {
                            allMusicsViewModel.handler.getArtistIdFromMusicId(it)
                        },
                        musicState = playerMusicState,
                        playlistState = playlistState,
                        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                        navigateToModifyMusic = {
                            navController.navigate("modifyMusic/$it")
                        },
                        playbackManager = playbackManager
                    )

                    PlayerMusicListView(
                        coverList = coversState.covers,
                        musicState = playerMusicListState,
                        playlistState = playlistState,
                        onMusicEvent = playerMusicListViewModel.handler::onMusicEvent,
                        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
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
        mainActivityViewModel.handler.isReadPermissionGranted = SoulSearchingContext.checkIfReadPermissionGranted()
        mainActivityViewModel.handler.isPostNotificationGranted = SoulSearchingContext.checkIfPostNotificationGranted()
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
            allMusicsViewModel.handler.checkAndDeleteMusicIfNotExist()
        } catch (_:RuntimeException) {

        }
    }
}
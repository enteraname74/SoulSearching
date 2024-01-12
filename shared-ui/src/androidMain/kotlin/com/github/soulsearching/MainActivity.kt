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
import com.github.soulsearching.classes.utils.AndroidUtils
import com.github.soulsearching.composables.MissingPermissionsComposable
import com.github.soulsearching.composables.NavigationHandler
import com.github.soulsearching.composables.appfeatures.FetchingMusicsComposable
import com.github.soulsearching.composables.player.PlayerDraggableView
import com.github.soulsearching.composables.player.PlayerMusicListView
import com.github.soulsearching.composables.remembers.rememberPlayerDraggableState
import com.github.soulsearching.composables.remembers.rememberPlayerMusicDraggableState
import com.github.soulsearching.composables.remembers.rememberSearchDraggableState
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.navigation.Route
import com.github.soulsearching.navigation.RoutesNames
import com.github.soulsearching.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.playback.PlayerService
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.AllAlbumsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllArtistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllImageCoversViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllPlaylistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllQuickAccessViewModelAndroidImpl
import com.github.soulsearching.viewmodel.MainActivityViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyAlbumViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyArtistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyMusicViewModelAndroidImpl
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.NavigationViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerMusicListViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedAlbumViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedArtistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModelAndroidImpl
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
    private lateinit var allFoldersViewModel: SettingsAllFoldersViewModelAndroidImpl
    private lateinit var addMusicsViewModel: SettingsAddMusicsViewModelAndroidImpl

    private lateinit var mainActivityViewModel: MainActivityViewModelAndroidImpl
    private lateinit var navigationViewModel: NavigationViewModelAndroidImpl

    private val settings: SoulSearchingSettings by inject<SoulSearchingSettings>()
    private val playbackManager: PlaybackManagerAndroidImpl by inject<PlaybackManagerAndroidImpl>()
    private val colorThemeManager: ColorThemeManager by inject<ColorThemeManager>()

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MAIN ACTIVITY", "BROADCAST RECEIVE INFO TO RELAUNCH SERVICE")
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
    private fun initializePlayerViewModel() {
        PlayerUtils.playerViewModel = playerViewModel
        PlayerUtils.playerViewModel.handler.retrieveCoverMethod =
            allImageCoversViewModel.handler::getImageCover
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

            initializeSharedPreferences()
            initializePlayerViewModel()
            initializeBroadcastReceive()

            mainActivityViewModel = koinViewModel()
            navigationViewModel = koinViewModel()
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
                            settings.setBoolean(
                                SoulSearchingSettings.HAS_MUSICS_BEEN_FETCHED_KEY,
                                true
                            )
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
//                    val navController = rememberNavController()
                    val navigationController = navigationViewModel.handler.navigationController
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

                    NavigationHandler(
//                        navController = navController,
                        navigationController = navigationController,
                        playerDraggableState = playerDraggableState,
                        searchDraggableState = searchDraggableState,
                        musicState = musicState,
                        playlistState = playlistState,
                        albumState = albumState,
                        artistState = artistState,
                        quickAccessState = quickAccessState,
                        allPlaylistsViewModel = allPlaylistsViewModel,
                        allImageCoversViewModel = allImageCoversViewModel,
                        modifyArtistViewModel = modifyArtistViewModel,
                        modifyAlbumViewModel = modifyAlbumViewModel,
                        modifyMusicViewModel = modifyMusicViewModel,
                        modifyPlaylistViewModel = modifyPlaylistViewModel,
                        selectedArtistViewModel = selectedArtistsViewModel,
                        selectedAlbumViewModel = selectedAlbumViewModel,
                        selectedPlaylistViewModel = selectedPlaylistViewModel,
                        settingsAllFoldersViewModel = allFoldersViewModel,
                        addMusicsViewModel = addMusicsViewModel,
                        allArtistsViewModel = allArtistsViewModel,
                        allAlbumsViewModel = allAlbumsViewModel,
                        allMusicsViewModel = allMusicsViewModel,
                        allFoldersViewModel = allFoldersViewModel,
                        playerMusicListViewModel = playerMusicListViewModel,
                        playerMusicListDraggableState = musicListDraggableState,
//                        navigationViewModel = navigationViewModel
                    )

                    PlayerDraggableView(
                        maxHeight = maxHeight,
                        draggableState = playerDraggableState,
                        retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                        musicListDraggableState = musicListDraggableState,
                        playerMusicListViewModel = playerMusicListViewModel,
                        onMusicEvent = PlayerUtils.playerViewModel.handler::onMusicEvent,
                        isMusicInFavoriteMethod = allMusicsViewModel.handler::isMusicInFavorite,
                        navigateToArtist = {
                            navigationController.navigateTo(
                                route = Route(
                                    route = RoutesNames.SELECTED_ARTIST_SCREEN,
                                    arguments = mapOf(Pair("artistId", it))
                                )
                            )
                        },
                        navigateToAlbum = {
                            navigationController.navigateTo(
                                route = Route(
                                    route = RoutesNames.SELECTED_ALBUM_SCREEN,
                                    arguments = mapOf(Pair("albumId", it))
                                )
                            )
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
                            navigationController.navigateTo(
                                route = Route(
                                    route = RoutesNames.MODIFY_MUSIC_SCREEN,
                                    arguments = mapOf(Pair("musicId", it))
                                )
                            )
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
                            navigationController.navigateTo(
                                route = Route(
                                    route = RoutesNames.MODIFY_MUSIC_SCREEN,
                                    arguments = mapOf(Pair("musicId", it))
                                )
                            )
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
        mainActivityViewModel.handler.isReadPermissionGranted =
            SoulSearchingContext.checkIfReadPermissionGranted()
        mainActivityViewModel.handler.isPostNotificationGranted =
            SoulSearchingContext.checkIfPostNotificationGranted()
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
        } catch (_: RuntimeException) {

        }
    }
}
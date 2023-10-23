package com.github.soulsearching.composables


import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.SettingsUtils
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates
import com.github.soulsearching.classes.enumsAndTypes.MusicBottomSheetState
import com.github.soulsearching.classes.enumsAndTypes.PlaylistType
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.playlistComposables.PlaylistPanel
import com.github.soulsearching.composables.searchComposables.SearchMusics
import com.github.soulsearching.composables.searchComposables.SearchView
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistScreen(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    playerMusicListViewModel: PlayerMusicListViewModel,
    title: String,
    image: Bitmap?,
    navigateToModifyPlaylist: () -> Unit = {},
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    playerSwipeableState: SwipeableState<BottomSheetStates>,
    playlistId: UUID?,
    updateNbPlayedAction: (UUID) -> Unit,
    playlistType: PlaylistType,
) {
    val orientation = LocalConfiguration.current.orientation
    val coroutineScope = rememberCoroutineScope()

    val searchSwipeableState = rememberSwipeableState(
        BottomSheetStates.COLLAPSED
    )

    var hasPlaylistPaletteBeenFetched by rememberSaveable {
        mutableStateOf(false)
    }

    image?.let {
        if (!hasPlaylistPaletteBeenFetched && SettingsUtils.settingsViewModel.isPersonalizedDynamicPlaylistThemeOn()) {
            SettingsUtils.settingsViewModel.setPlaylistColorPalette(it)
            hasPlaylistPaletteBeenFetched = true
        }
    }

    BackHandler(playerSwipeableState.currentValue != BottomSheetStates.EXPANDED) {
        navigateBack()
    }

    val shuffleAction = {
        if (musicState.musics.isNotEmpty()) {
            coroutineScope
                .launch {
                    playerSwipeableState.animateTo(
                        BottomSheetStates.EXPANDED,
                        tween(Constants.AnimationTime.normal)
                    )
                }
                .invokeOnCompletion {
                    PlayerUtils.playerViewModel.playShuffle(
                        musicState.musics,
                        playerMusicListViewModel::savePlayerMusicList
                    )
                }
        }
    }

    val searchAction = {
        coroutineScope
            .launch {
                searchSwipeableState.animateTo(
                    BottomSheetStates.EXPANDED,
                    tween(Constants.AnimationTime.long)
                )
            }
    }

    val musicBottomSheetState = when (playlistType) {
        PlaylistType.PLAYLIST -> MusicBottomSheetState.PLAYLIST
        PlaylistType.ALBUM -> MusicBottomSheetState.ALBUM_OR_ARTIST
        PlaylistType.ARTIST -> MusicBottomSheetState.ALBUM_OR_ARTIST
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        AppHeaderBar(
                            title = title,
                            leftAction = navigateBack,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Constants.Spacing.large),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppImage(
                                bitmap = image,
                                size = Constants.ImageSize.huge,
                                roundedPercent = 5
                            )
                        }
                    }
                    PlaylistPanel(
                        editAction = navigateToModifyPlaylist,
                        shuffleAction = {
                            playlistId?.let(updateNbPlayedAction)
                            shuffleAction()
                        },
                        searchAction = { searchAction() },
                        isLandscapeMode = true,
                        playlistType = playlistType,
                    )
                    MusicList(
                        musicBottomSheetState = musicBottomSheetState,
                        musicState = musicState,
                        playlistState = playlistState,
                        onMusicEvent = onMusicEvent,
                        onPlaylistEvent = onPlaylistEvent,
                        navigateToModifyMusic = navigateToModifyMusic,
                        retrieveCoverMethod = { retrieveCoverMethod(it) },
                        swipeableState = playerSwipeableState,
                        playlistId = playlistId,
                        playerMusicListViewModel = playerMusicListViewModel,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        playerSwipeableState = playerSwipeableState,
                        updateNbPlayedAction = updateNbPlayedAction,
                    )
                }
            }
            else -> {
                MusicBottomSheetEvents(
                    musicBottomSheetState = musicBottomSheetState,
                    musicState = musicState,
                    playlistState = playlistState,
                    onMusicEvent = onMusicEvent,
                    onPlaylistsEvent = onPlaylistEvent,
                    navigateToModifyMusic = navigateToModifyMusic,
                    playerMusicListViewModel = playerMusicListViewModel,
                    playerSwipeableState = playerSwipeableState,
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AppHeaderBar(
                        title = title,
                        leftAction = navigateBack,
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Constants.Spacing.large),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppImage(
                                    bitmap = image,
                                    size = Constants.ImageSize.huge,
                                    roundedPercent = 5
                                )
                            }
                        }
                        stickyHeader {
                            PlaylistPanel(
                                editAction = navigateToModifyPlaylist,
                                shuffleAction = {
                                    playlistId?.let(updateNbPlayedAction)
                                    shuffleAction()
                                },
                                searchAction = { searchAction() },
                                isLandscapeMode = false,
                                playlistType = playlistType,
                            )
                        }
                        items(
                            items = musicState.musics
                        ) { elt ->
                            MusicItemComposable(
                                primaryColor = DynamicColor.primary,
                                textColor = DynamicColor.onPrimary,
                                music = elt,
                                onClick = { music ->
                                    coroutineScope.launch {
                                        playerSwipeableState.animateTo(BottomSheetStates.EXPANDED)
                                    }.invokeOnCompletion {
                                        playlistId?.let {
                                            updateNbPlayedAction(it)
                                        }

                                        if (!PlayerUtils.playerViewModel.isSamePlaylist(
                                                false,
                                                playlistId
                                            )
                                        ) {
                                            playerMusicListViewModel.savePlayerMusicList(musicState.musics.map { it.musicId } as ArrayList<UUID>)
                                        }
                                        PlayerUtils.playerViewModel.setCurrentPlaylistAndMusic(
                                            music = music,
                                            playlist = musicState.musics,
                                            playlistId = playlistId,
                                            isMainPlaylist = false
                                        )
                                    }
                                },
                                onLongClick = {
                                    coroutineScope.launch {
                                        onMusicEvent(
                                            MusicEvent.SetSelectedMusic(
                                                elt
                                            )
                                        )
                                        onMusicEvent(
                                            MusicEvent.BottomSheet(
                                                isShown = true
                                            )
                                        )
                                    }
                                },
                                musicCover = retrieveCoverMethod(elt.coverId),
                            )
                        }
                        item { PlayerSpacer() }
                    }
                }
            }
        }

        SearchView(
            swipeableState = searchSwipeableState,
            maxHeight = maxHeight,
            placeholder = stringResource(id = R.string.search_for_musics),
            playerSwipeableState = playerSwipeableState,
        ) { searchText, focusManager ->
            SearchMusics(
                playerSwipeableState = playerSwipeableState,
                searchText = searchText,
                musicState = musicState,
                onMusicEvent = onMusicEvent,
                playerMusicListViewModel = playerMusicListViewModel,
                isMainPlaylist = false,
                focusManager = focusManager,
                retrieveCoverMethod = retrieveCoverMethod,
            )
        }
    }
}
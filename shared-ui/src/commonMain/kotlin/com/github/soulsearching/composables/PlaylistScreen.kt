package com.github.soulsearching.composables


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.Constants
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.remembers.rememberSearchDraggableState
import com.github.soulsearching.composables.search.SearchMusics
import com.github.soulsearching.composables.search.SearchView
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.draggablestates.PlayerDraggableState
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.strings
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.types.PlaylistType
import com.github.soulsearching.types.ScreenOrientation
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistScreen(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    playerMusicListViewModel: PlayerMusicListViewModel,
    title: String,
    image: ImageBitmap?,
    navigateToModifyPlaylist: () -> Unit = {},
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    playerDraggableState: PlayerDraggableState,
    playlistId: UUID?,
    updateNbPlayedAction: (UUID) -> Unit,
    playlistType: PlaylistType,
    colorThemeManager: ColorThemeManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    var hasPlaylistPaletteBeenFetched by remember {
        mutableStateOf(false)
    }

    image?.let {
        if (!hasPlaylistPaletteBeenFetched && colorThemeManager.isPersonalizedDynamicPlaylistThemeOn()) {
            colorThemeManager.setNewPlaylistCover(it)
            hasPlaylistPaletteBeenFetched = true
        }
    }

    if (!hasPlaylistPaletteBeenFetched && colorThemeManager.isPersonalizedDynamicPlaylistThemeOff()) {
        colorThemeManager.forceBasicThemeForPlaylists = true
        hasPlaylistPaletteBeenFetched = true
    }

    SoulSearchingBackHandler(playerDraggableState.state.currentValue != BottomSheetStates.EXPANDED) {
        navigateBack()
    }

    val shuffleAction = {
        if (musicState.musics.isNotEmpty()) {
            playlistId?.let(updateNbPlayedAction)
            coroutineScope
                .launch {
                    playerDraggableState.animateTo(BottomSheetStates.EXPANDED)
                }
                .invokeOnCompletion {
                    PlayerUtils.playerViewModel.handler.playShuffle(
                        musicState.musics,
                        playerMusicListViewModel.handler::savePlayerMusicList
                    )
                }
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
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        val searchDraggableState = rememberSearchDraggableState(constraintsScope = this)

        val searchAction = {
            coroutineScope
                .launch {
                    searchDraggableState.state.animateTo(BottomSheetStates.EXPANDED)
                }
        }

        when (SoulSearchingContext.orientation) {
            ScreenOrientation.HORIZONTAL -> {
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
                            shuffleAction()
                        },
                        searchAction = { searchAction() },
                        isLandscapeMode = true,
                        playlistType = playlistType,
                    )
                    MusicList(
                        musicState = musicState,
                        playlistState = playlistState,
                        onMusicEvent = onMusicEvent,
                        onPlaylistEvent = onPlaylistEvent,
                        playerMusicListViewModel = playerMusicListViewModel,
                        navigateToModifyMusic = navigateToModifyMusic,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        retrieveCoverMethod = { retrieveCoverMethod(it) },
                        playlistId = playlistId,
                        musicBottomSheetState = musicBottomSheetState,
                        playerDraggableState = playerDraggableState,
                        updateNbPlayedAction = updateNbPlayedAction,
                    )
                }
            }

            else -> {
                MusicBottomSheetEvents(
                    musicState = musicState,
                    playlistState = playlistState,
                    onMusicEvent = onMusicEvent,
                    onPlaylistsEvent = onPlaylistEvent,
                    navigateToModifyMusic = navigateToModifyMusic,
                    musicBottomSheetState = musicBottomSheetState,
                    playerMusicListViewModel = playerMusicListViewModel,
                    playerDraggableState = playerDraggableState,
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
                                music = elt,
                                onClick = { music ->
                                    coroutineScope.launch {
                                        playerDraggableState.animateTo(BottomSheetStates.EXPANDED)
                                    }.invokeOnCompletion {
                                        playlistId?.let {
                                            updateNbPlayedAction(it)
                                        }

                                        if (!PlayerUtils.playerViewModel.handler.isSamePlaylist(
                                                false,
                                                playlistId
                                            )
                                        ) {
                                            playerMusicListViewModel.handler.savePlayerMusicList(musicState.musics.map { it.musicId } as ArrayList<UUID>)
                                        }
                                        PlayerUtils.playerViewModel.handler.setCurrentPlaylistAndMusic(
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
                                textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                            )
                        }
                        item { PlayerSpacer() }
                    }
                }
            }
        }

        SearchView(
            draggableState = searchDraggableState,
            playerDraggableState = playerDraggableState,
            placeholder = strings.searchForMusics,
        ) { searchText, focusManager ->
            SearchMusics(
                playerDraggableState = playerDraggableState,
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
package com.github.soulsearching.elementpage.playlistpage.presentation.composable


import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.soulsearching.Constants
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.soulsearching.domain.model.types.PlaylistType
import com.github.soulsearching.domain.model.types.ScreenOrientation
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.search.presentation.SearchMusics
import com.github.soulsearching.search.presentation.SearchView
import com.github.soulsearching.strings.strings
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Suppress("Deprecation")
@Composable
fun PlaylistScreen(
    playlistId: UUID?,
    playlistWithMusics: List<PlaylistWithMusics>,
    title: String,
    image: ImageBitmap?,
    musics: List<Music>,
    navigateToModifyPlaylist: () -> Unit = {},
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    updateNbPlayedAction: (UUID) -> Unit,
    playlistType: PlaylistType,
    isDeleteMusicDialogShown: Boolean,
    isBottomSheetShown: Boolean,
    isAddToPlaylistBottomSheetShown: Boolean,
    isRemoveFromPlaylistDialogShown: Boolean = false,
    onSetBottomSheetVisibility: (Boolean) -> Unit,
    onSetDeleteMusicDialogVisibility: (Boolean) -> Unit,
    onSetRemoveMusicFromPlaylistDialogVisibility: (Boolean) -> Unit = {},
    onSetAddToPlaylistBottomSheetVisibility: (Boolean) -> Unit = {},
    onDeleteMusic: (Music) -> Unit,
    onToggleQuickAccessState: (Music) -> Unit,
    onRemoveFromPlaylist: (Music) -> Unit = {},
    onAddMusicToSelectedPlaylists: (selectedPlaylistsIds: List<UUID>, selectedMusic: Music) -> Unit,
    colorThemeManager: ColorThemeManager = injectElement(),
    playbackManager: PlaybackManager = injectElement()
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

    SoulSearchingBackHandler(playerDraggableState.currentValue != BottomSheetStates.EXPANDED) {
        navigateBack()
    }

    val shuffleAction = {
        if (musics.isNotEmpty()) {
            playlistId?.let(updateNbPlayedAction)
            coroutineScope
                .launch {
                    playerDraggableState.animateTo(
                        BottomSheetStates.EXPANDED,
                        tween(Constants.AnimationDuration.normal)
                    )
                }
                .invokeOnCompletion {
                    playbackManager.playShuffle(musicList = musics)
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
        val searchDraggableState = rememberSwipeableState(
            initialValue = BottomSheetStates.COLLAPSED
        )

        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        val searchBarFocusRequester = remember { FocusRequester() }

        val searchAction = {
            coroutineScope.launch {
                    searchDraggableState.animateTo(
                        BottomSheetStates.EXPANDED,
                        tween(Constants.AnimationDuration.normal)
                    )
                }.invokeOnCompletion {
                    searchBarFocusRequester.requestFocus()
                }
        }

        var selectedMusicId by rememberSaveable {
            mutableStateOf<UUID?>(null)
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
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        selectedMusic = musics.find { it.musicId == selectedMusicId },
                        onSelectMusic = {
                            selectedMusicId = it.musicId
                        },
                        musics = musics,
                        playlistsWithMusics = playlistWithMusics,
                        playlistId = playlistId,
                        isDeleteMusicDialogShown = isDeleteMusicDialogShown,
                        isBottomSheetShown = isBottomSheetShown,
                        isAddToPlaylistBottomSheetShown = isAddToPlaylistBottomSheetShown,
                        isRemoveFromPlaylistDialogShown = isRemoveFromPlaylistDialogShown,
                        onSetBottomSheetVisibility = onSetBottomSheetVisibility,
                        onSetDeleteMusicDialogVisibility = onSetDeleteMusicDialogVisibility,
                        onSetRemoveMusicFromPlaylistDialogVisibility = onSetRemoveMusicFromPlaylistDialogVisibility,
                        onSetAddToPlaylistBottomSheetVisibility = onSetAddToPlaylistBottomSheetVisibility,
                        onDeleteMusic = onDeleteMusic,
                        onToggleQuickAccessState = onToggleQuickAccessState,
                        onRemoveFromPlaylist = onRemoveFromPlaylist,
                        onAddMusicToSelectedPlaylists = onAddMusicToSelectedPlaylists,
                        navigateToModifyMusic = navigateToModifyMusic,
                        retrieveCoverMethod = { retrieveCoverMethod(it) },
                        updateNbPlayedAction = updateNbPlayedAction,
                        musicBottomSheetState = musicBottomSheetState,
                        playerDraggableState = playerDraggableState
                    )
                }
            }

            else -> {
                musics.find { it.musicId == selectedMusicId }?.let { music ->
                    MusicBottomSheetEvents(
                        selectedMusic = music,
                        playlistsWithMusics = playlistWithMusics,
                        navigateToModifyMusic = navigateToModifyMusic,
                        musicBottomSheetState = musicBottomSheetState,
                        playerDraggableState = playerDraggableState,
                        isDeleteMusicDialogShown = isDeleteMusicDialogShown,
                        isBottomSheetShown = isBottomSheetShown,
                        isAddToPlaylistBottomSheetShown = isAddToPlaylistBottomSheetShown,
                        isRemoveFromPlaylistDialogShown = isRemoveFromPlaylistDialogShown,
                        onDismiss = {
                            onSetBottomSheetVisibility(false)
                        },
                        onSetDeleteMusicDialogVisibility = onSetDeleteMusicDialogVisibility,
                        onSetRemoveMusicFromPlaylistDialogVisibility = onSetRemoveMusicFromPlaylistDialogVisibility,
                        onSetAddToPlaylistBottomSheetVisibility = onSetAddToPlaylistBottomSheetVisibility,
                        onDeleteMusic = { onDeleteMusic(music) },
                        onToggleQuickAccessState = { onToggleQuickAccessState(music) },
                        onRemoveFromPlaylist = { onRemoveFromPlaylist(music) },
                        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds ->
                            onAddMusicToSelectedPlaylists(selectedPlaylistsIds, music)
                        },
                        retrieveCoverMethod = retrieveCoverMethod
                    )
                }

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
                            items = musics
                        ) { elt ->
                            MusicItemComposable(
                                music = elt,
                                onClick = { music ->
                                    coroutineScope.launch {
                                        playerDraggableState.animateTo(
                                            BottomSheetStates.EXPANDED,
                                            tween(Constants.AnimationDuration.normal)
                                        )
                                    }.invokeOnCompletion {
                                        playlistId?.let {
                                            updateNbPlayedAction(it)
                                        }
                                        playbackManager.setCurrentPlaylistAndMusic(
                                            music = music,
                                            musicList = musics,
                                            playlistId = playlistId,
                                            isMainPlaylist = false
                                        )
                                    }
                                },
                                onLongClick = {
                                    coroutineScope.launch {
                                        selectedMusicId = elt.musicId
                                        onSetBottomSheetVisibility(true)
                                    }
                                },
                                musicCover = retrieveCoverMethod(elt.coverId),
                                textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                                isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(elt.musicId)
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
            maxHeight = maxHeight,
            focusRequester = searchBarFocusRequester
        ) { searchText, focusManager ->
            SearchMusics(
                playerDraggableState = playerDraggableState,
                searchText = searchText,
                allMusics = musics,
                isMainPlaylist = false,
                focusManager = focusManager,
                retrieveCoverMethod = retrieveCoverMethod,
                onSelectedMusicForBottomSheet = {
                    selectedMusicId = it.musicId
                    onSetBottomSheetVisibility(true)
                }
            )
        }
    }
}
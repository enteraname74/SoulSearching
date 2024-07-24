package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation.composable


import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.elementpage.composable.PageHeader
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistType
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.SearchMusics
import com.github.enteraname74.soulsearching.feature.search.SearchView
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Suppress("Deprecation")
@Composable
fun PlaylistScreen(
    playlistId: UUID?,
    playlistName: String,
    playlistCover: ImageBitmap?,
    musics: List<Music>,
    navigateToModifyPlaylist: () -> Unit = {},
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    updateNbPlayedAction: (UUID) -> Unit,
    playlistType: PlaylistType,
    onShowMusicBottomSheet: (Music) -> Unit,
    colorThemeManager: ColorThemeManager = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    var hasPlaylistPaletteBeenFetched by remember {
        mutableStateOf(false)
    }

    playlistCover?.let {
        if (!hasPlaylistPaletteBeenFetched && colorThemeManager.isPersonalizedDynamicPlaylistThemeOn()) {
            colorThemeManager.setNewPlaylistCover(it)
            hasPlaylistPaletteBeenFetched = true
        }
    }

    if (!hasPlaylistPaletteBeenFetched && colorThemeManager.isPersonalizedDynamicPlaylistThemeOff()) {
        colorThemeManager.forceBasicThemeForPlaylists = true
        hasPlaylistPaletteBeenFetched = true
    }

    SoulBackHandler(playerViewManager.currentValue != BottomSheetStates.EXPANDED) {
        navigateBack()
    }

    val shuffleAction = {
        if (musics.isNotEmpty()) {
            playlistId?.let(updateNbPlayedAction)
            coroutineScope
                .launch {
                    playerViewManager.animateTo(
                        newState = BottomSheetStates.EXPANDED,
                    )
                }
                .invokeOnCompletion {
                    playbackManager.playShuffle(musicList = musics)
                }
        }
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
                    tween(UiConstants.AnimationDuration.normal)
                )
            }.invokeOnCompletion {
                searchBarFocusRequester.requestFocus()
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
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = strings.backButton,
                                tint = SoulSearchingColorTheme.colorScheme.onPrimary
                            )
                        }
                        PageHeader(
                            title = playlistName,
                            cover = playlistCover,
                            text = strings.musics(musics.size)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        PlaylistPanel(
                            editAction = navigateToModifyPlaylist,
                            shuffleAction = {
                                shuffleAction()
                            },
                            searchAction = { searchAction() },
                            playlistType = playlistType,
                        )
                        MusicList(
                            modifier = Modifier
                                .fillMaxHeight(),
                            musics = musics,
                            playlistId = playlistId,
                            retrieveCoverMethod = { retrieveCoverMethod(it) },
                            updateNbPlayedAction = updateNbPlayedAction,
                            playerViewManager = playerViewManager,
                            onShowMusicBottomSheet = onShowMusicBottomSheet
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = strings.backButton,
                            tint = SoulSearchingColorTheme.colorScheme.onPrimary
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            PageHeader(
                                title = playlistName,
                                cover = playlistCover,
                                text = strings.musics(musics.size)
                            )
                        }
                        stickyHeader {
                            PlaylistPanel(
                                editAction = navigateToModifyPlaylist,
                                shuffleAction = {
                                    playlistId?.let(updateNbPlayedAction)
                                    shuffleAction()
                                },
                                searchAction = { searchAction() },
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
                                        playerViewManager.animateTo(
                                            newState = BottomSheetStates.EXPANDED,
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
                                onLongClick = { onShowMusicBottomSheet(elt) },
                                musicCover = retrieveCoverMethod(elt.coverId),
                                textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                                isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(elt.musicId)
                            )
                        }
                        item { SoulPlayerSpacer() }
                    }
                }
            }
        }

        SearchView(
            draggableState = searchDraggableState,
            placeholder = strings.searchForMusics,
            maxHeight = maxHeight,
            focusRequester = searchBarFocusRequester
        ) { searchText, focusManager ->
            SearchMusics(
                playerViewManager = playerViewManager,
                searchText = searchText,
                allMusics = musics,
                isMainPlaylist = false,
                focusManager = focusManager,
                retrieveCoverMethod = retrieveCoverMethod,
                onSelectedMusicForBottomSheet = onShowMusicBottomSheet
            )
        }
    }
}
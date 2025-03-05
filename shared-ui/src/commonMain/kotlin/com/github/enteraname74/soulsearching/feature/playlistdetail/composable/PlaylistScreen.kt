package com.github.enteraname74.soulsearching.feature.playlistdetail.composable


import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionScaffold
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.AnimatedColorPaletteBuilder
import com.github.enteraname74.soulsearching.coreui.theme.color.LocalColors
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingPalette
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.view.PlaylistLargeView
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.view.PlaylistSmallView
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.view.PlaylistRowView
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistViewUiUtils
import com.github.enteraname74.soulsearching.feature.search.SearchMusics
import com.github.enteraname74.soulsearching.feature.search.SearchView
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.PlaylistDetailCover
import com.github.enteraname74.soulsearching.theme.orDefault
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Suppress("Deprecation")
@Composable
fun PlaylistScreen(
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    multiSelectionManagerImpl: MultiSelectionManagerImpl,
    navigateBack: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    multiSelectionState: MultiSelectionState,
    onLongSelectOnMusic: (Music) -> Unit,
    colorThemeManager: ColorThemeManager = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    optionalContent: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(playerViewManager.currentValue) {
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
            playlistDetailListener.onCloseSelection()
        }
    }

    LaunchedEffect(playlistDetail.cover) {
        if (playlistDetail.cover?.isEmpty() != false) {
            colorThemeManager.setNewPlaylistCover(
                playlistDetailCover = PlaylistDetailCover.NoCover
            )
        }
    }

    var bitmap: ImageBitmap? by remember {
        mutableStateOf(null)
    }

    val hasFoundImage by derivedStateOf {
        bitmap != null
    }

    LaunchedEffect(hasFoundImage) {
        colorThemeManager.setNewPlaylistCover(
            playlistDetailCover = PlaylistDetailCover.fromImageBitmap(bitmap)
        )
    }

    val onCoverLoaded: (ImageBitmap?) -> Unit = {
        bitmap = it
    }

    SoulBackHandler(playerViewManager.currentValue != BottomSheetStates.EXPANDED) {
        navigateBack()
    }

    val shuffleAction = {
        if (playlistDetail.musics.isNotEmpty()) {
            playlistDetailListener.onUpdateNbPlayed()
            coroutineScope.launch {
                playbackManager.playShuffle(musicList = playlistDetail.musics)
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    val playAction = {
        if (playlistDetail.musics.isNotEmpty()) {
            playlistDetailListener.onUpdateNbPlayed()
            coroutineScope.launch {
                playbackManager.setCurrentPlaylistAndMusic(
                    music = playlistDetail.musics.first(),
                    musicList = playlistDetail.musics,
                    playlistId = playlistDetail.id,
                    isMainPlaylist = false
                )
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    val playlistPalette: SoulSearchingPalette? by colorThemeManager.playlistsColorTheme.collectAsState()

    CompositionLocalProvider(
        LocalColors provides AnimatedColorPaletteBuilder.animate(playlistPalette.orDefault())
    ) {
        MultiSelectionScaffold(
            multiSelectionManagerImpl = multiSelectionManagerImpl,
            onCancel = playlistDetailListener::onCloseSelection,
            onMore = playlistDetailListener::onMoreClickedOnSelection,
        ) {
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
                    constraintsScope.maxHeight.toPx() + getNavigationBarPadding()
                }

                val searchBarFocusRequester = remember { FocusRequester() }

                val searchAction: () -> Unit = {
                    coroutineScope.launch {
                        multiSelectionManagerImpl.clearMultiSelection()
                        searchDraggableState.animateTo(
                            BottomSheetStates.EXPANDED,
                            tween(UiConstants.AnimationDuration.normal)
                        )
                        searchBarFocusRequester.requestFocus()
                    }
                }

                val windowSize = rememberWindowSize()
                when (windowSize) {
                    WindowSize.Small -> {
                        PlaylistSmallView(
                            navigateBack = navigateBack,
                            playAction = playAction,
                            shuffleAction = shuffleAction,
                            searchAction = searchAction,
                            onShowMusicBottomSheet = onShowMusicBottomSheet,
                            playlistDetail = playlistDetail,
                            playlistDetailListener = playlistDetailListener,
                            optionalContent = optionalContent,
                            onCoverLoaded = onCoverLoaded,
                            onLongSelectOnMusic = onLongSelectOnMusic,
                            multiSelectionState = multiSelectionState,
                        )
                    }

                    else -> {
                        if (PlaylistViewUiUtils.canShowColumnLayout()) {
                            PlaylistLargeView(
                                navigateBack = navigateBack,
                                shuffleAction = shuffleAction,
                                playAction = playAction,
                                searchAction = searchAction,
                                onShowMusicBottomSheet = onShowMusicBottomSheet,
                                playlistDetail = playlistDetail,
                                playlistDetailListener = playlistDetailListener,
                                optionalContent = optionalContent,
                                onCoverLoaded = onCoverLoaded,
                                onLongSelectOnMusic = onLongSelectOnMusic,
                                multiSelectionState = multiSelectionState,
                            )
                        } else {
                            PlaylistRowView(
                                navigateBack = navigateBack,
                                playAction = playAction,
                                shuffleAction = shuffleAction,
                                searchAction = searchAction,
                                onShowMusicBottomSheet = onShowMusicBottomSheet,
                                playlistDetail = playlistDetail,
                                playlistDetailListener = playlistDetailListener,
                                optionalContent = optionalContent,
                                onCoverLoaded = onCoverLoaded,
                                onLongSelectOnMusic = onLongSelectOnMusic,
                                multiSelectionState = multiSelectionState,
                            )
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
                        searchText = searchText,
                        allMusics = playlistDetail.musics,
                        isMainPlaylist = false,
                        focusManager = focusManager,
                        onSelectedMusicForBottomSheet = onShowMusicBottomSheet,
                    )
                }
            }
        }
    }
}


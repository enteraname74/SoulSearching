package com.github.enteraname74.soulsearching.feature.elementpage.composable


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
import com.github.enteraname74.domain.model.getFromCoverId
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.coversprovider.ImageCoverRetriever
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.SearchMusics
import com.github.enteraname74.soulsearching.feature.search.SearchView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Suppress("Deprecation")
@Composable
fun PlaylistScreen(
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    navigateBack: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    colorThemeManager: ColorThemeManager = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    imageCoverRetriever: ImageCoverRetriever = injectElement(),
    optionalContent: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val imageState by imageCoverRetriever.allCovers.collectAsState()

    var hasPlaylistPaletteBeenFetched by remember {
        mutableStateOf(false)
    }

    val playlistDetailCover: ImageBitmap? by imageCoverRetriever.getImageBitmap(coverId = playlistDetail.coverId)
        .collectAsState(initial = null)

    playlistDetailCover.let {
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
        if (playlistDetail.musics.isNotEmpty()) {
            playlistDetailListener.onUpdateNbPlayed()
            coroutineScope
                .launch {
                    playerViewManager.animateTo(
                        newState = BottomSheetStates.EXPANDED,
                    )
                }
                .invokeOnCompletion {
                    playbackManager.playShuffle(musicList = playlistDetail.musics)
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

        val searchAction: () -> Unit = {
            coroutineScope.launch {
                searchDraggableState.animateTo(
                    BottomSheetStates.EXPANDED,
                    tween(UiConstants.AnimationDuration.normal)
                )
            }.invokeOnCompletion {
                searchBarFocusRequester.requestFocus()
            }
        }

        val windowSize = rememberWindowSize()
        when {
            windowSize != WindowSize.Small -> {
                PlaylistRowView(
                    navigateBack = navigateBack,
                    shuffleAction = shuffleAction,
                    searchAction = searchAction,
                    onShowMusicBottomSheet = onShowMusicBottomSheet,
                    playlistDetail = playlistDetail,
                    playlistDetailListener = playlistDetailListener,
                    optionalContent = optionalContent,
                )
            }
            else -> {
                PlaylistColumnView(
                    navigateBack = navigateBack,
                    shuffleAction = shuffleAction,
                    searchAction = searchAction,
                    onShowMusicBottomSheet = onShowMusicBottomSheet,
                    playlistDetail = playlistDetail,
                    playlistDetailListener = playlistDetailListener,
                    optionalContent = optionalContent,
                )
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
                allMusics = playlistDetail.musics,
                isMainPlaylist = false,
                focusManager = focusManager,
                retrieveCoverMethod = imageState::getFromCoverId,
                onSelectedMusicForBottomSheet = onShowMusicBottomSheet
            )
        }
    }
}


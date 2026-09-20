package com.github.enteraname74.soulsearching.feature.musiclistdetail

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
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
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.MusicListDetailUiUtils
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.view.PlaylistLargeView
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.view.PlaylistRowView
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.view.PlaylistSmallView
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.PlaylistSearchViewManager
import com.github.enteraname74.soulsearching.feature.search.SearchMusics
import com.github.enteraname74.soulsearching.feature.search.SearchView
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.PlaylistDetailCover
import com.github.enteraname74.soulsearching.theme.orDefault
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Suppress("Deprecation")
@Composable
fun MusicListDetailDataScreen(
    data: MusicListDetailState.Data,
    playlistSearchViewManager: PlaylistSearchViewManager,
    multiSelectionState: MultiSelectionState,
    multiSelectionManager: MultiSelectionManager = injectElement(),
    colorThemeManager: ColorThemeManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    SoulBackHandler(
        enabled = multiSelectionManager.isActive(),
    ) {
        multiSelectionManager.clearMultiSelection()
    }

    LaunchedEffect(playerViewManager.currentValue) {
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
            data.onCloseSelection()
        }
    }

    LaunchedEffect(data.cover) {
        if (data.cover?.isEmpty() != false) {
            colorThemeManager.setNewPlaylistCover(
                playlistDetailCover = PlaylistDetailCover.NoCover
            )
        }
    }

    val playlistPalette: SoulSearchingPalette? by colorThemeManager.playlistsColorTheme.collectAsState()

    CompositionLocalProvider(
        LocalColors provides AnimatedColorPaletteBuilder.animate(playlistPalette.orDefault())
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(SoulSearchingColorTheme.colorScheme.primary)
        ) {
            val constraintsScope = this
            val maxHeight = with(LocalDensity.current) {
                constraintsScope.maxHeight.toPx() + getNavigationBarPadding()
            }

            val searchBarFocusRequester = remember { FocusRequester() }

            val openSearchView: () -> Unit = {
                coroutineScope.launch {
                    data.onCloseSelection()
                    playlistSearchViewManager.animateTo(
                        newState = BottomSheetStates.EXPANDED,
                        onExpanded = {
                            searchBarFocusRequester.requestFocus()
                        }
                    )
                }
            }

            val windowSize = rememberWindowSize()
            when (windowSize) {
                WindowSize.Small -> {
                    PlaylistSmallView(
                        data = data,
                        openSearchView = openSearchView,
                        multiSelectionState = multiSelectionState,
                    )
                }

                else -> {
                    if (MusicListDetailUiUtils.canShowColumnLayout()) {
                        PlaylistLargeView(
                            data = data,
                            openSearchView = openSearchView,
                            multiSelectionState = multiSelectionState,
                        )
                    } else {
                        PlaylistRowView(
                            data = data,
                            openSearchView = openSearchView,
                            multiSelectionState = multiSelectionState,
                        )
                    }
                }
            }

            SearchView(
                searchViewManager = playlistSearchViewManager,
                placeholder = strings.searchForMusics,
                maxHeight = maxHeight,
                focusRequester = searchBarFocusRequester,
                onSearch = data.onSearch,
            ) { focusManager, lazyListState ->
                SearchMusics(
                    lazyListState = lazyListState,
                    foundMusics = data.searchMusics,
                    isMainPlaylist = false,
                    focusManager = focusManager,
                    onSelectedMusicForBottomSheet = {
                        data.showMusicBottomSheet(it)
                    },
                )
            }
        }
    }
}


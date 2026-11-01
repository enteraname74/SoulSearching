package com.github.enteraname74.soulsearching.feature.musiclistdetail

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.theme.color.AnimatedColorPaletteBuilder
import com.github.enteraname74.soulsearching.coreui.theme.color.LocalColors
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingPalette
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
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
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.PlaylistDetailCover
import com.github.enteraname74.soulsearching.theme.orDefault
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Suppress("Deprecation")
@Composable
fun MusicListDetailDataScreen(
    data: MusicListDetailState.Data,
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
        SoulScreen {
            val openSearchView: () -> Unit = {
                coroutineScope.launch {
                    data.onCloseSelection()
                    data.openSearch()
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
        }
    }
}


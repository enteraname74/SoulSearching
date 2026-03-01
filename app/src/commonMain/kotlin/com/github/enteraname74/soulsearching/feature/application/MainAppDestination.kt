package com.github.enteraname74.soulsearching.feature.application


import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.enteraname74.soulsearching.PlayerViewScaffold
import com.github.enteraname74.soulsearching.composables.bottomsheets.album.AlbumBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.artist.ArtistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanel
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings_filled
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.ext.isComingFromPlaylistDetails
import com.github.enteraname74.soulsearching.ext.filledIcon
import com.github.enteraname74.soulsearching.ext.outlinedIcon
import com.github.enteraname74.soulsearching.ext.text
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionScaffold
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsDestination
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.navigation.MainAppNavigationHandler
import com.github.enteraname74.soulsearching.navigation.MainAppSerializerModule
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object MainAppDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
    ) {
        entryProviderScope.entry<MainAppDestination> {
            MainAppRoute()
        }
    }
}

@Composable
private fun MainAppRoute(
    playbackManager: PlaybackManager = injectElement(),
    playerViewModel: PlayerViewModel = koinViewModel(),
    mainPageViewModel: MainPageViewModel = koinViewModel(),
) {
    val tabs: List<PagerScreen> by mainPageViewModel.tabs.collectAsState()
    val currentElementPage: ElementEnum? by mainPageViewModel.currentPage.collectAsState()
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { serializersModule = MainAppSerializerModule },
        MainPageDestination,
    )
    val navigator = remember { Navigator(backStack) }

    val shouldShowNewVersionPin: Boolean by mainPageViewModel.shouldShowNewVersionPin.collectAsState()

    LaunchInit {
        playbackManager.initFromSavedData()
    }

    Row {
        val windowSize = rememberWindowSize()

        if (windowSize == WindowSize.Large) {
            NavigationPanel(
                rows = navigationRows(
                    shouldShowNewVersionPin = shouldShowNewVersionPin,
                    navigator = navigator,
                    setCurrentPage = mainPageViewModel::setCurrentPage,
                    tabs = tabs,
                    currentPage = currentElementPage,
                )
            )
        }

        MultiSelectionScaffold(
            onNavigationState = { navigationState ->
                when (navigationState) {
                    MultiSelectionNavigationState.Idle -> {
                        // no-op
                    }
                    is MultiSelectionNavigationState.ToMusicBottomSheet -> {
                        navigator.push(
                            MusicBottomSheetDestination(
                                musicIds = navigationState.musicIds,
                                playlistId = navigationState.playlistId,
                            )
                        )
                    }

                    is MultiSelectionNavigationState.ToPlaylistBottomSheet -> {
                        navigator.push(
                            PlaylistBottomSheetDestination(
                                playlistIds = navigationState.playlistIds,
                            )
                        )
                    }

                    is MultiSelectionNavigationState.ToAlbumBottomSheet -> {
                        navigator.push(
                            AlbumBottomSheetDestination(
                                albumIds = navigationState.albumIds,
                            )
                        )
                    }
                    is MultiSelectionNavigationState.ToArtistBottomSheet -> {
                        navigator.push(
                            ArtistBottomSheetDestination(
                                artistIds = navigationState.artistIds,
                            )
                        )
                    }
                }

            }
        ) {
            PlayerViewScaffold(
                navigator = navigator,
                playerViewModel = playerViewModel,
            ) {
                MainAppNavigationHandler(
                    navigator = navigator,
                    backStack = backStack,
                )
            }
        }
    }
}

@Composable
private fun navigationRows(
    shouldShowNewVersionPin: Boolean,
    setCurrentPage: (ElementEnum) -> Unit,
    currentPage: ElementEnum?,
    tabs: List<PagerScreen>,
    navigator: Navigator,
    playerViewManager: PlayerViewManager = injectElement(),
    colorThemeManager: ColorThemeManager = injectElement(),
): List<NavigationRowSpec> {
    val coroutineScope = rememberCoroutineScope()

    val playerAction: () -> Unit = {
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
            coroutineScope.launch {
                playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
            }
        }
    }

    return buildList {
        add(
            NavigationRowSpec(
                title = strings.settings,
                onClick = {
                    playerAction()
                    if (navigator.isComingFromPlaylistDetails()) {
                        colorThemeManager.removePlaylistTheme()
                    }
                    navigator.push(SettingsDestination)
                },
                filledIcon = CoreRes.drawable.ic_settings_filled,
                outlinedIcon = CoreRes.drawable.ic_settings,
                isSelected = navigator.currentRoute is SettingPage,
                isBadged = shouldShowNewVersionPin,
            )
        )
        tabs.forEachIndexed { index, tab ->

            val pageCheck: Boolean = (currentPage == null && index == 0) || (currentPage == tab.type)

            add(
                NavigationRowSpec(
                    title = tab.type.text(),
                    filledIcon = tab.type.filledIcon(),
                    outlinedIcon = tab.type.outlinedIcon(),
                    onClick = {
                        setCurrentPage(tab.type)
                        playerAction()
                        if (navigator.isComingFromPlaylistDetails()) {
                            colorThemeManager.removePlaylistTheme()
                        }
                        navigator.push(MainPageDestination)
                    },
                    isSelected = (navigator.currentRoute is MainPageDestination) && pageCheck
                )
            )
        }
    }
}
package com.github.enteraname74.soulsearching.feature.application

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.enteraname74.soulsearching.PlayerViewScaffold
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanel
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.ext.isComingFromPlaylistDetails
import com.github.enteraname74.soulsearching.ext.navigationFilledIcon
import com.github.enteraname74.soulsearching.ext.navigationOutlinedIcon
import com.github.enteraname74.soulsearching.ext.navigationTitle
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsDestination
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.navigation.MainAppSerializerModule
import com.github.enteraname74.soulsearching.navigation.MainAppNavigationHandler
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
                    navigator.navigate(SettingsDestination)
                },
                filledIcon = Icons.Rounded.Settings,
                outlinedIcon = Icons.Outlined.Settings,
                isSelected = navigator.currentRoute is SettingPage,
                isBadged = shouldShowNewVersionPin,
            )
        )
        tabs.forEachIndexed { index, tab ->

            val pageCheck: Boolean = (currentPage == null && index == 0) || (currentPage == tab.type)

            add(
                NavigationRowSpec(
                    title = tab.type.navigationTitle(),
                    filledIcon = tab.type.navigationFilledIcon(),
                    outlinedIcon = tab.type.navigationOutlinedIcon(),
                    onClick = {
                        setCurrentPage(tab.type)
                        playerAction()
                        if (navigator.isComingFromPlaylistDetails()) {
                            colorThemeManager.removePlaylistTheme()
                        }
                        navigator.navigate(MainPageDestination)
                    },
                    isSelected = (navigator.currentRoute is MainPageDestination) && pageCheck
                )
            )
        }
    }
}
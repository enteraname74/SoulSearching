package com.github.enteraname74.soulsearching.feature.application

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.github.enteraname74.soulsearching.PlayerViewScaffold
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanel
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.ext.*
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingFeature
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageScreen
import com.github.enteraname74.soulsearching.feature.migration.MigrationScreen
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsScreen
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.launch

class ApplicationWindow: Screen {
    @Composable
    override fun Content() {
        val applicationViewModel: ApplicationViewModel = koinScreenModel()
        val mainPageViewModel: MainPageViewModel = koinScreenModel()
        val playerViewModel: PlayerViewModel = koinScreenModel()

        val loadingManager: LoadingManager = injectElement()
        val isLoadingManagerLoading by loadingManager.state.collectAsState()

        val state: ApplicationState by applicationViewModel.state.collectAsState()

        val shouldShowNewVersionPin: Boolean by mainPageViewModel.shouldShowNewVersionPin.collectAsState()

        when(state) {
            ApplicationState.AppMigration -> {
                MigrationScreen()
            }
            ApplicationState.Data -> {
                DataView(
                    isLoadingManagerLoading = isLoadingManagerLoading,
                    mainPageViewModel = mainPageViewModel,
                    playerViewModel = playerViewModel,
                    shouldShowNewVersionPin = shouldShowNewVersionPin,
                )
            }
            ApplicationState.FetchingSongs -> {
                AppInitSongFetchingFeature()
            }
        }
    }

    @Composable
    private fun DataView(
        shouldShowNewVersionPin: Boolean,
        playerViewModel: PlayerViewModel,
        mainPageViewModel: MainPageViewModel,
        isLoadingManagerLoading: Boolean,
        playbackManager: PlaybackManager = injectElement(),
    ) {
        val tabs: List<PagerScreen> by mainPageViewModel.tabs.collectAsState()
        val currentElementPage: ElementEnum? by mainPageViewModel.currentPage.collectAsState()
        var generalNavigator: Navigator? by remember { mutableStateOf(null) }

        LaunchInit {
            playbackManager.initFromSavedData()
        }

        Row {
            val windowSize = rememberWindowSize()

            if (windowSize == WindowSize.Large) {
                NavigationPanel(
                    rows = navigationRows(
                        shouldShowNewVersionPin = shouldShowNewVersionPin,
                        generalNavigator = generalNavigator,
                        setCurrentPage = mainPageViewModel::setCurrentPage,
                        tabs = tabs,
                        currentPage = currentElementPage,
                    )
                )
            }

            PlayerViewScaffold(
                generalNavigator = generalNavigator,
                playerViewModel = playerViewModel,
            ) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues = WindowInsets.navigationBars.asPaddingValues())
                ) {
                    Navigator(
                        screen = MainPageScreen(),
                        onBackPressed = {
                            !isLoadingManagerLoading
                        }
                    ) { navigator ->
                        generalNavigator = navigator

                        CrossfadeTransition(
                            navigator = navigator,
                            animationSpec = tween(UiConstants.AnimationDuration.normal)
                        ) { screen ->
                            screen.Content()
                        }
                    }
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
        playerViewManager: PlayerViewManager = injectElement(),
        colorThemeManager: ColorThemeManager = injectElement(),
        generalNavigator: Navigator?,
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
                        if (generalNavigator?.isComingFromPlaylistDetails() == true) {
                            colorThemeManager.removePlaylistTheme()
                        }
                        generalNavigator?.safePush(
                            SettingsScreen()
                        )
                    },
                    filledIcon = Icons.Rounded.Settings,
                    outlinedIcon = Icons.Outlined.Settings,
                    isSelected = generalNavigator?.lastItem is SettingPage,
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
                            if (generalNavigator?.isComingFromPlaylistDetails() == true) {
                                colorThemeManager.removePlaylistTheme()
                            }
                            generalNavigator?.safePush(
                                MainPageScreen()
                            )
                        },
                        isSelected = (generalNavigator?.lastItem is MainPageScreen) && pageCheck
                    )
                )
            }
        }
    }
}
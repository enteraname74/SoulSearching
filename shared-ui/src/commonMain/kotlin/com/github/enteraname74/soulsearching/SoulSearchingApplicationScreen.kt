package com.github.enteraname74.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanel
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpScaffold
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingScaffold
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.ext.isComingFromPlaylistDetails
import com.github.enteraname74.soulsearching.ext.navigationIcon
import com.github.enteraname74.soulsearching.ext.navigationTitle
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.appinit.FetchingMusicsComposable
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ApplicationState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageNavigationState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.ApplicationViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageScreen
import com.github.enteraname74.soulsearching.feature.migration.MigrationScreen
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceScreen
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsScreen
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SoulSearchingApplicationScreen: Screen, KoinComponent {
    private val feedbackPopUpManager: FeedbackPopUpManager by inject()
    private val loadingManager: LoadingManager by inject()
    private val playbackManager: PlaybackManager by inject()

    @Composable
    override fun Content() {
        val mainPageViewModel = koinScreenModel<MainPageViewModel>()
        val applicationViewModel = koinScreenModel<ApplicationViewModel>()

        val state: ApplicationState by applicationViewModel.state.collectAsState()
        val navigationState: MainPageNavigationState by mainPageViewModel.navigationState.collectAsState()

        val tabs: List<PagerScreen> by mainPageViewModel.tabs.collectAsState()
        val currentElementPage: ElementEnum? by mainPageViewModel.currentPage.collectAsState()
        var generalNavigator: Navigator? by remember { mutableStateOf(null) }

        var hasPlaybackBeenInitialized: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(hasPlaybackBeenInitialized) {
            if (!hasPlaybackBeenInitialized) {
                playbackManager.initFromSavedData()
                hasPlaybackBeenInitialized = true
            }
        }

        LaunchedEffect(navigationState) {
            if (navigationState is MainPageNavigationState.ToArtistChoice) {
                navigator.replaceAll(MultipleArtistsChoiceScreen())
            }
        }

        SoulSearchingAppTheme {

            when (state) {
                ApplicationState.AppMigration -> {
                    MigrationScreen()
                }
                ApplicationState.FetchingSongs -> {
                    FetchingMusicsComposable(
                        mainPageViewModel = mainPageViewModel
                    )
                }

                ApplicationState.Data -> {
                    FeedbackPopUpScaffold(
                        feedbackPopUpManager = feedbackPopUpManager,
                    ) {
                        LoadingScaffold(
                            loadingManager = loadingManager
                        ) { isLoading ->
                            Row {
                                val windowSize = rememberWindowSize()

                                if (windowSize == WindowSize.Large) {
                                    NavigationPanel(
                                        rows = navigationRows(
                                            generalNavigator = generalNavigator,
                                            setCurrentPage = mainPageViewModel::setCurrentPage,
                                            tabs = tabs,
                                            currentPage = currentElementPage,
                                        )
                                    )
                                }

                                PlayerViewScaffold(
                                    generalNavigator = generalNavigator,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(paddingValues = WindowInsets.navigationBars.asPaddingValues())
                                    ) {
                                        Navigator(
                                            screen = MainPageScreen(),
                                            onBackPressed = {
                                                !isLoading
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
                    }
                }
            }
        }
    }

    @Composable
    private fun navigationRows(
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
                    icon = Icons.Rounded.Settings,
                    isSelected = generalNavigator?.lastItem is SettingsScreen
                )
            )
            tabs.forEachIndexed { index, tab ->

                val pageCheck: Boolean = (currentPage == null && index == 0) || (currentPage == tab.type)

                add(
                    NavigationRowSpec(
                        title = tab.type.navigationTitle(),
                        icon = tab.type.navigationIcon(),
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
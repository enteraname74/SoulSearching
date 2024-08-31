package com.github.enteraname74.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.github.enteraname74.domain.model.getFromCoverId
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanel
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpScaffold
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
import com.github.enteraname74.soulsearching.feature.coversprovider.ImageCoverRetriever
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainActivityViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageScreen
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsScreen
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SoulSearchingApplication(
    settings: SoulSearchingSettings = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    feedbackPopUpManager: FeedbackPopUpManager = injectElement(),
    imageCoverRetriever: ImageCoverRetriever = injectElement<ImageCoverRetriever>(),
) {
    val mainPageViewModel = injectElement<MainPageViewModel>()
    val mainActivityViewModel = injectElement<MainActivityViewModel>()

    val musicState by mainPageViewModel.allMusicsState.collectAsState()
    val tabs: List<PagerScreen> by mainPageViewModel.tabs.collectAsState()
    val currentElementPage: ElementEnum? by mainPageViewModel.currentPage.collectAsState()
    val allImages by imageCoverRetriever.allCovers.collectAsState()

    playbackManager.retrieveCoverMethod = allImages::getFromCoverId

    if (allImages.isNotEmpty()) {
        LaunchedEffect("Covers check") {
            CoroutineScope(Dispatchers.IO).launch {
                playbackManager.currentMusic?.let { currentMusic ->
                    playbackManager.defineCoverAndPaletteFromCoverId(
                        coverId = currentMusic.coverId
                    )
                    playbackManager.update()
                }
            }
        }
    }

    if (!mainActivityViewModel.hasMusicsBeenFetched) {
        FetchingMusicsComposable(
            finishAddingMusicsAction = {
                settings.set(
                    SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
                    true
                )
                mainActivityViewModel.hasMusicsBeenFetched = true
            },
            mainPageViewModel = mainPageViewModel
        )
        return
    }

    if (musicState.musics.isNotEmpty() && !mainActivityViewModel.cleanMusicsLaunched) {
        mainPageViewModel.checkAndDeleteMusicIfNotExist()
        mainActivityViewModel.cleanMusicsLaunched = true
    }

    var generalNavigator: Navigator? by remember { mutableStateOf(null) }

    SoulSearchingAppTheme {
        FeedbackPopUpScaffold(
            feedbackPopUpManager = feedbackPopUpManager,
        ) {
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
                    var hasLastPlayedMusicsBeenFetched by rememberSaveable {
                        mutableStateOf(false)
                    }

                    if (!hasLastPlayedMusicsBeenFetched) {
                        LaunchedEffect(key1 = "FETCH_LAST_PLAYED_LIST") {
                            val playerSavedMusics = playbackManager.getSavedPlayedList()
                            if (playerSavedMusics.isNotEmpty()) {
                                playbackManager.initializePlayerFromSavedList(playerSavedMusics)
                                playerViewManager.animateTo(
                                    newState = BottomSheetStates.MINIMISED,
                                )
                            }
                            hasLastPlayedMusicsBeenFetched = true
                        }
                    }

                    Navigator(MainPageScreen()) { navigator ->
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

@file:Suppress("Deprecation")

package com.github.enteraname74.soulsearching.feature.mainpage.presentation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionScaffold
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageNavigationState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.SearchAllState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainMenuHeaderComposable
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageHorizontalShortcut
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageVerticalShortcut
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.SearchAll
import com.github.enteraname74.soulsearching.feature.search.SearchView
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainPageRoute(
    mainPageViewModel: MainPageViewModel = koinViewModel(),
    onNavigation: (MainPageNavigationState) -> Unit,
) {
    val playerViewManager: PlayerViewManager = injectElement()

    val searchAllState: SearchAllState by mainPageViewModel.searchAllState.collectAsStateWithLifecycle()

    val tabs: List<PagerScreen> by mainPageViewModel.tabs.collectAsStateWithLifecycle()
    val currentPage: ElementEnum? by mainPageViewModel.currentPage.collectAsStateWithLifecycle()
    val isUsingVerticalAccessBar: Boolean by mainPageViewModel.isUsingVerticalAccessBar.collectAsStateWithLifecycle()
    val shouldShowNewVersionPin: Boolean by mainPageViewModel.shouldShowNewVersionPin.collectAsStateWithLifecycle()

    val searchDraggableState = mainPageViewModel.searchDraggableState

    val bottomSheetState: SoulBottomSheet? by mainPageViewModel.bottomSheetState.collectAsStateWithLifecycle()
    val addToPlaylistsBottomSheetState: AddToPlaylistBottomSheet? by mainPageViewModel.addToPlaylistsBottomSheetState.collectAsStateWithLifecycle()
    val dialogState: SoulDialog? by mainPageViewModel.dialogState.collectAsStateWithLifecycle()
    val navigationState: MainPageNavigationState by mainPageViewModel.navigationState.collectAsStateWithLifecycle()
    bottomSheetState?.BottomSheet()
    addToPlaylistsBottomSheetState?.BottomSheet()
    dialogState?.Dialog()

    LaunchedEffect(playerViewManager.currentValue) {
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
            mainPageViewModel.clearMultiSelection()
        }
    }

    LaunchedEffect(navigationState) {
        onNavigation(navigationState)
        mainPageViewModel.consumeNavigation()
    }

    MainPageScreenView(
        mainPageViewModel = mainPageViewModel,
        searchDraggableState = searchDraggableState,
        tabs = tabs,
        currentEnumPage = currentPage,
        isUsingVerticalAccessBar = isUsingVerticalAccessBar,
        shouldShowNewVersionPin = shouldShowNewVersionPin,
        searchAllState = searchAllState,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Suppress("Deprecation")
fun MainPageScreenView(
    mainPageViewModel: MainPageViewModel,
    searchDraggableState: SwipeableState<BottomSheetStates>,
    searchAllState: SearchAllState,
    tabs: List<PagerScreen>,
    shouldShowNewVersionPin: Boolean,
    currentEnumPage: ElementEnum?,
    isUsingVerticalAccessBar: Boolean,
) {
    val pagerState = rememberPagerState(
        pageCount = { tabs.size }
    )

    LaunchedEffect(currentEnumPage) {
        val vmCurrentPage: Int = tabs.indexOfFirst { it.type == currentEnumPage }
        if (vmCurrentPage != pagerState.currentPage && vmCurrentPage != -1) {
            pagerState.animateScrollToPage(vmCurrentPage)
        }
    }

    val searchBarFocusRequester = remember { FocusRequester() }
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    MultiSelectionScaffold(
        multiSelectionManagerImpl = mainPageViewModel.multiSelectionManagerImpl,
        onCancel = mainPageViewModel::clearMultiSelection,
        onMore = mainPageViewModel::handleMultiSelectionBottomSheet,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SoulSearchingColorTheme.colorScheme.primary)
        ) {
            val constraintsScope = this
            val maxHeight = with(LocalDensity.current) {
                constraintsScope.maxHeight.toPx() + getNavigationBarPadding()
            }

            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary)
            ) {
                MainMenuHeaderComposable(
                    shouldShowNewReleasePin = shouldShowNewVersionPin,
                    settingsAction = mainPageViewModel::toSettings,
                    searchAction = {
                        coroutineScope.launch {
                            searchDraggableState.animateTo(
                                BottomSheetStates.EXPANDED,
                                tween(UiConstants.AnimationDuration.normal)
                            )
                        }.invokeOnCompletion {
                            searchBarFocusRequester.requestFocus()
                        }
                    }
                )

                if (isUsingVerticalAccessBar) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // We only show the vertical shortcut if there is more than one panel to access.
                        if (shouldShowShortcutAccess(tabs = tabs)) {
                            MainPageVerticalShortcut(
                                currentPage = currentPage,
                                visibleElements = tabs.map { it.type },
                                switchPageAction = { newPage ->
                                    if (newPage == -1) return@MainPageVerticalShortcut
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(newPage)
                                    }.invokeOnCompletion {
                                        mainPageViewModel.setCurrentPage(tabs[newPage].type)
                                    }
                                }
                            )
                        }
                        VerticalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            userScrollEnabled = false,
                        ) {
                            tabs[it].screen()
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        if (shouldShowShortcutAccess(tabs = tabs)) {
                            MainPageHorizontalShortcut(
                                currentPage = currentPage,
                                visibleElements = tabs.map { it.type },
                                switchPageAction = { newPage ->
                                    if (newPage == -1) return@MainPageHorizontalShortcut
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(newPage)
                                    }.invokeOnCompletion {
                                        mainPageViewModel.setCurrentPage(tabs[newPage].type)
                                    }
                                }
                            )
                        }

                        val windowSize = rememberWindowSize()

                        if (windowSize == WindowSize.Large) {
                            VerticalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize(),
                                userScrollEnabled = false,
                            ) {
                                tabs[it].screen()
                            }
                        } else {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                tabs[it].screen()
                            }
                        }
                    }
                }
            }

            SearchView(
                draggableState = searchDraggableState,
                placeholder = strings.searchAll,
                maxHeight = maxHeight,
                focusRequester = searchBarFocusRequester,
                onSearch = mainPageViewModel::onSearch,
            ) { focusManager ->
                SearchAll(
                    searchAllState = searchAllState,
                    onSelectedMusicForBottomSheet = mainPageViewModel::showMusicBottomSheet,
                    onSelectedAlbumForBottomSheet = mainPageViewModel::showAlbumPreviewBottomSheet,
                    onSelectedPlaylistForBottomSheet = mainPageViewModel::showPlaylistPreviewBottomSheet,
                    onSelectedArtistForBottomSheet = mainPageViewModel::showArtistPreviewBottomSheet,
                    navigateToPlaylist = mainPageViewModel::toPlaylist,
                    navigateToArtist = mainPageViewModel::toArtist,
                    navigateToAlbum = mainPageViewModel::toAlbum,
                    isMainPlaylist = false,
                    focusManager = focusManager,
                )
            }
        }
    }
}

@Composable
private fun shouldShowShortcutAccess(tabs: List<PagerScreen>): Boolean {
    val windowSize = rememberWindowSize()
    return tabs.size > 1 && windowSize != WindowSize.Large
}


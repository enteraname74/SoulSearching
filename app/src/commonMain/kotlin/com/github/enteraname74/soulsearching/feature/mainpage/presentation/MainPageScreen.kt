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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
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
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.*
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

    val musicState: AllMusicsState by mainPageViewModel.allMusicsState.collectAsState()
    val playlistState: AllPlaylistsState by mainPageViewModel.allPlaylistsState.collectAsState()
    val albumState: AllAlbumsState by mainPageViewModel.allAlbumsState.collectAsState()
    val artistState: AllArtistsState by mainPageViewModel.allArtistsState.collectAsState()

    val tabs: List<PagerScreen> by mainPageViewModel.tabs.collectAsState()
    val currentPage: ElementEnum? by mainPageViewModel.currentPage.collectAsState()
    val isUsingVerticalAccessBar: Boolean by mainPageViewModel.isUsingVerticalAccessBar.collectAsState()
    val shouldShowNewVersionPin: Boolean by mainPageViewModel.shouldShowNewVersionPin.collectAsState()

    val searchDraggableState = mainPageViewModel.searchDraggableState

    val bottomSheetState: SoulBottomSheet? by mainPageViewModel.bottomSheetState.collectAsState()
    val addToPlaylistsBottomSheetState: AddToPlaylistBottomSheet? by mainPageViewModel.addToPlaylistsBottomSheetState.collectAsState()
    val dialogState: SoulDialog? by mainPageViewModel.dialogState.collectAsState()
    val navigationState: MainPageNavigationState by mainPageViewModel.navigationState.collectAsState()
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
        musicState = musicState,
        allPlaylistsState = playlistState,
        allAlbumsState = albumState,
        allArtistsState = artistState,
        tabs = tabs,
        currentEnumPage = currentPage,
        isUsingVerticalAccessBar = isUsingVerticalAccessBar,
        shouldShowNewVersionPin = shouldShowNewVersionPin,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Suppress("Deprecation")
fun MainPageScreenView(
    mainPageViewModel: MainPageViewModel,
    searchDraggableState: SwipeableState<BottomSheetStates>,
    musicState: AllMusicsState,
    allPlaylistsState: AllPlaylistsState,
    allAlbumsState: AllAlbumsState,
    allArtistsState: AllArtistsState,
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
                focusRequester = searchBarFocusRequester
            ) { searchText, focusManager ->
                SearchAll(
                    searchText = searchText,
                    musicState = musicState,
                    allAlbumsState = allAlbumsState,
                    allArtistsState = allArtistsState,
                    allPlaylistsState = allPlaylistsState,
                    onSelectedMusicForBottomSheet = mainPageViewModel::showMusicBottomSheet,
                    onSelectedAlbumForBottomSheet = mainPageViewModel::showAlbumBottomSheet,
                    onSelectedPlaylistForBottomSheet = mainPageViewModel::showPlaylistBottomSheet,
                    onSelectedArtistForBottomSheet = mainPageViewModel::showArtistBottomSheet,
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


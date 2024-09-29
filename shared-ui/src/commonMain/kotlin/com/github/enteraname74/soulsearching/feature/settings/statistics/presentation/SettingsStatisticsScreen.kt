package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.ListenedElement
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.SettingsStatisticsState
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.SettingsStatisticsViewModel
import com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable.SettingsStatisticsSection
import com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable.SettingsStatisticsSectionIndicatorList
import kotlinx.coroutines.launch

class SettingsStatisticsScreen: Screen {

    @Composable
    override fun Content() {
        val screenModel: SettingsStatisticsViewModel = koinScreenModel()
        val state: SettingsStatisticsState by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        DataScreen(
            state = state,
            navigateBack = { navigator.pop() }
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun DataScreen(
        state: SettingsStatisticsState,
        navigateBack: () -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()

        SoulScreen {
            Column {
                SoulTopBar(
                    title = strings.statisticsTitle,
                    leftAction = TopBarNavigationAction(
                        onClick = navigateBack,
                    )
                )
                val pagerState = rememberPagerState(
                    pageCount = { TOTAL_PAGES }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SettingsStatisticsSectionIndicatorList(
                        selectedIndex = pagerState.currentPage,
                        listSize = pagerState.pageCount,
                        onClick = { selectedIndex ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = selectedIndex,
                                )
                            }
                        }
                    )
                }
                HorizontalPager(
                    modifier = Modifier,
                    state = pagerState,
                ) {index ->
                    SettingsStatisticsSection(
                        title = state.title(index),
                        elements = state.elements(index),
                    )
                }
            }
        }
    }
}

private fun SettingsStatisticsState.elements(index: Int): List<ListenedElement> =
    when (index) {
        0 -> mostListenedMusics
        1 -> mostListenedArtists
        2 -> artistsWithMostSongs
        3 -> mostListenedAlbums
        4 -> mostListenedPlaylists
        else -> mostListenedMusics
    }

@Composable
private fun SettingsStatisticsState.title(index: Int): String =
    when(index) {
        0 -> strings.mostPlayedSongs
        1 -> strings.mostPlayedArtists
        2 -> strings.artistsWithMostSongs
        3 -> strings.mostPlayedAlbums
        4 -> strings.mostPlayedPlaylists
        else -> strings.mostPlayedSongs
    }

private const val TOTAL_PAGES: Int = 5
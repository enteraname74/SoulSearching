package com.github.enteraname74.soulsearching.feature.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.usecase.ShouldInformOfNewReleaseUseCase
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.SearchAllViewManager
import com.github.enteraname74.soulsearching.feature.tabmanager.TabManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainAppViewModel(
    shouldInformOfNewReleaseUseCase: ShouldInformOfNewReleaseUseCase,
    private val tabManager: TabManager,
    private val playerViewManager: PlayerViewManager,
    private val navScope: MainAppNavScope,
    private val searchAllViewManager: SearchAllViewManager,
    private val playbackManager: PlaybackManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : ViewModel() {
    val state: StateFlow<MainAppState> = combine(
        tabManager.tabs,
        tabManager.currentPage,
        shouldInformOfNewReleaseUseCase()
    ) { tabs, currentPage, shouldShowNewVersionPin ->
        MainAppState(
            navigationRows = buildNavigationRows(
                shouldShowNewVersionPin = shouldShowNewVersionPin,
                currentPage = currentPage,
                tabs = tabs,
            )
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainAppState(),
        started = SharingStarted.Eagerly,
    )

    init {
        viewModelScope.launch {
            playbackManager.playbackError.filterNotNull().collectLatest {
                feedbackPopUpManager.showFeedback(strings.playbackErrorPlayerError)
                playbackManager.consumeError()
            }
        }
    }

    private fun buildNavigationRows(
        shouldShowNewVersionPin: Boolean,
        currentPage: ElementEnum?,
        tabs: List<ElementEnum>,
    ): List<NavigationRowSpec> = buildList {
        add(
            NavigationRowSpec.Settings(
                onClick = {
                    playerViewManager.minimiseIfPossible()
                    navScope.toSettings()
                },
                isBadged = shouldShowNewVersionPin,
            )
        )
        tabs.forEachIndexed { index, tab ->

            val pageCheck: Boolean = (currentPage == null && index == 0) || (currentPage == tab)

            add(
                NavigationRowSpec.MainTabElement(
                    element = tab,
                    isCurrentPage = pageCheck,
                    onClick = {
                        searchAllViewManager.closeIfPossible()
                        playerViewManager.minimiseIfPossible()
                        navScope.toMainPageDestinationIfNeeded()
                        tabManager.setCurrentPage(tab)
                    },
                )
            )
        }
    }
}

package com.github.enteraname74.soulsearching.feature.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.domain.usecase.ShouldInformOfNewReleaseUseCase
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.tabmanager.TabManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainAppViewModel(
    shouldInformOfNewReleaseUseCase: ShouldInformOfNewReleaseUseCase,
    private val tabManager: TabManager,
    private val playerViewManager: PlayerViewManager,
    private val navScope: MainAppNavScope,
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
                        playerViewManager.minimiseIfPossible()
                        navScope.toMainPageDestinationIfNeeded()
                        tabManager.setCurrentPage(tab)
                    },
                )
            )
        }
    }
}

package com.github.enteraname74.soulsearching.feature.tabmanager

import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TabManager(
    viewSettingsManager: ViewSettingsManager,
    settingsRepository: SettingsRepository,
    workDispatcher: WorkDispatcher,
) {
    private var _currentPage: MutableStateFlow<ElementEnum?> = MutableStateFlow(null)
    val currentPage: Flow<ElementEnum?> = _currentPage

    @OptIn(ExperimentalCoroutinesApi::class)
    val tabs: Flow<List<ElementEnum>> = viewSettingsManager.visibleElements.map {
        it.toElementEnums()
    }

    init {
        CoroutineScope(workDispatcher.dispatcher).launch {
            val visibleElements = tabs.first()
            val savedInitial = ElementEnum.fromRaw(settingsRepository.getSettings().mainPage.initialTab)
                ?.takeIf { it in visibleElements }
            _currentPage.value = savedInitial ?: visibleElements.firstOrNull()
        }
    }

    fun setCurrentPage(page: ElementEnum) {
        _currentPage.value = page
    }
}

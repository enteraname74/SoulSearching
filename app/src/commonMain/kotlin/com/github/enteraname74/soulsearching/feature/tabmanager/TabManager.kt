package com.github.enteraname74.soulsearching.feature.tabmanager

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.math.max

class TabManager(
    viewSettingsManager: ViewSettingsManager,
    settings: SoulSearchingSettings,
) {
    private var _currentPage: MutableStateFlow<ElementEnum?> = MutableStateFlow(null)
    val currentPage: Flow<ElementEnum?> = _currentPage

    @OptIn(ExperimentalCoroutinesApi::class)
    val tabs: Flow<List<ElementEnum>> = viewSettingsManager.visibleElements.map {
        it.toElementEnums()
    }

    val savedInitial: ElementEnum? = ElementEnum.fromRaw(
        settings.get(ElementEnum.INITIAL_TAB_SETTINGS_KEY)
    ).also {
        it?.let { setCurrentPage(it) }
    }
    val initialPage: Int = max(
        viewSettingsManager.getElementVisibility().toElementEnums().indexOf(savedInitial),
        0
    )

    fun setCurrentPage(page: ElementEnum) {
        _currentPage.value = page
    }
}
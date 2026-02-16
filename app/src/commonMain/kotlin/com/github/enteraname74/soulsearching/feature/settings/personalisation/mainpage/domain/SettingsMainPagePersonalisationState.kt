package com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain

import com.github.enteraname74.soulsearching.domain.model.ElementsVisibility
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum

sealed interface SettingsMainPagePersonalisationState {
    data object Loading: SettingsMainPagePersonalisationState
    data class Data(
        val elementsVisibility: ElementsVisibility,
        val isUsingVerticalAccessBar: Boolean,
        val selectableTabs: List<ElementEnum>,
        val initialTab: ElementEnum,
    ): SettingsMainPagePersonalisationState
}
